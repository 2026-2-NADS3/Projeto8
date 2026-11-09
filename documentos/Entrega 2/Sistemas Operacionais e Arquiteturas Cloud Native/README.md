# Sistema de Backup Automatizado com Docker

## Descrição

Este projeto consiste em um sistema de backup automatizado de um banco de dados MySQL usando containers Docker. O projeto usa Docker Compose para gerenciar os containers responsáveis pelo banco de dados e o script de backups.

O sistema faz backups periódicos do banco, armazena os arquivos em um volume persistente e remove os backups mais antigos, mantendo a quantidade configurada. O projeto é uma evolução do script de backup da primeira entrega, agora containerizado.

## Arquitetura

A aplicação é composta por dois containers gerenciados pelo Docker Compose:
 
- `db`: executa o banco de dados MySQL 8.4 (imagem oficial).
- `backup`: executa o script de backup, construído a partir do `Dockerfile` próprio.

```text
              Docker Compose
                    |
              Rede interna
                    |
          +---------+---------+
          |                   |
     +---------+         +-----------+
     |   db    | <------ |  backup   |
     |  MySQL  |         |  Bash     |
     +---------+         +-----------+
          |                   |
     mysql_data           backup_data
       (volume)              (volume)
```

## Funcionamento
 
1. O container `db` inicia o MySQL.
2. O healthcheck verifica por TCP se o banco está pronto.
3. O container `backup` inicia quando o `db` está saudável.
4. O `entrypoint.sh` executa o `backup.sh` nos intervalos definidos.
5. O `backup.sh` confirma que o banco responde e gera um arquivo `.sql` com `mysqldump`.
6. O arquivo é salvo no volume `backup_data`.
7. Os backups que ultrapassam a quantidade definida são removidos.
8. As operações são registradas nos logs do container, com data e hora.

## Imagem Docker
 
O `Dockerfile` do serviço de backup usa `ubuntu:24.04` como base. Ela foi escolhida por por fornecer o pacote `mysql-client-8.0`, compatível com o servidor MySQL 8.4.
 
Boas práticas aplicadas:
 
- `--no-install-recommends` na instalação, instalando somente o necessário (cliente MySQL e `findutils`).
- limpeza do cache do `apt` (`rm -rf /var/lib/apt/lists/*`) na mesma camada da instalação.
- instalação de pacotes antes da cópia dos scripts, aproveitando o cache de camadas ao alterar apenas os scripts.
- `.dockerignore` para evitar a cópia de arquivos desnecessários para a imagem.


## Requisitos
 
No computador (host):
 
- Linux
- Docker
- Docker Compose

As outras ferramentas (`bash`, `mysqldump`, `mysqladmin`, `find`, `sort`, `rm`, `date`, `sleep`, `mkdir`) já estão dentro dos containers e não precisam ser instaladas.

## Configuração
 
Crie o arquivo `.env` a partir do modelo:
 
```bash
cp .env.example .env
```
 
Edite o `.env` e altere as senhas. As variáveis são:
 
- `MYSQL_ROOT_PASSWORD`: senha do usuário root do MySQL.
- `MYSQL_DATABASE`: nome do banco de dados.
- `MYSQL_USER`: usuário usado pelo serviço de backup.
- `MYSQL_PASSWORD`: senha desse usuário.
- `BACKUP_INTERVAL`: intervalo entre os backups, em segundos.
- `RETENTION_COUNT`: quantidade de backups mais recentes a manter.

O `.env.example` serve de modelo, com valores de exemplo.

## Como executar
 
Construir as imagens e iniciar os containers:
 
```bash
docker compose up -d --build
```
 
Acompanhar os logs do serviço de backup:
 
```bash
docker compose logs -f backup
```
 
Listar os backups gerados:
 
```bash
docker compose exec backup ls -lh /backups
```
 
Parar e remover os containers (os volumes são mantidos):
 
```bash
docker compose down
```

O comando `docker compose down -v` remove os volumes também, apagando os dados do banco e todos os backups.

## Exemplo de uso
 
Com a configuração abaixo no `.env`:
 
```env
BACKUP_INTERVAL=60
RETENTION_COUNT=5
```
 
o serviço executa um backup a cada 60 segundos e mantém os cinco mais recentes.

Saída esperada nos logs:
 
```text
backup-service  | Iniciando serviço de backup...
backup-service  | Intervalo entre backups: 60 segundos.
backup-service  | Iniciando nova execução...
backup-service  | mysqld is alive
backup-service  | [2026-11-08 19:27:10] Backup criado com sucesso: /backups/backup_2026-11-08_19-27-10.sql
backup-service  | [2026-11-08 19:27:10] Backup antigo removido: /backups/backup_2026-11-08_19-11-20.sql
backup-service  | [2026-11-08 19:27:10] Operação de backup concluída.
backup-service  | Próxima execução em 60 segundos.
```

## Rede e portas
 
Os dois serviços ficam na rede padrão criada pelo Docker Compose e se comunicam pelo nome do serviço: o container `backup` acessa o banco usando `db` como endereço (`MYSQL_HOST: db`).
 
- A porta 3306 do MySQL é usada internamente. Ela não é publicada no host, então o banco não fica acessível pelo computador hospedeiro.
- O serviço `backup` não expõe nenhuma porta.

## Volumes e persistência
 
- `mysql_data`: dados do banco MySQL (`/var/lib/mysql`).
- `backup_data`: arquivos de backup (`/backups`).

Os dados permanecem mesmo após a remoção dos containers.

## Retenção de backups
 
A variável `RETENTION_COUNT` define quantos backups são mantidos. Com `RETENTION_COUNT=5`, ao ser criado o sexto backup, o mais antigo é removido, e isso é registrado no log.

## Como testar
 
### 1. Containers em execução
 
```bash
docker compose ps
```
 
O `db` deve aparecer como `healthy` e o `backup` como `Up`.
 
### 2. Backup com dados reais
 
Crie uma tabela com dados no banco:
 
```bash
docker compose exec db sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" -e "
CREATE TABLE IF NOT EXISTS clientes (id INT PRIMARY KEY AUTO_INCREMENT, nome VARCHAR(50));
INSERT INTO clientes (nome) VALUES (\"Ana\"), (\"Bruno\"), (\"Carla\");"'
```
 
Aguarde o próximo backup (até `BACKUP_INTERVAL` segundos) e confira o conteúdo do mais recente:
 
```bash
docker compose exec backup sh -c 'ls -lh /backups && grep "INSERT INTO" $(ls -t /backups/*.sql | head -1)'
```
 
Saída esperada (lista de arquivos seguida do `INSERT`):
 
```text
total 20K
-rw-r--r-- 1 root root 2.0K Nov  8 19:57 backup_2026-11-08_19-57-11.sql
-rw-r--r-- 1 root root 2.0K Nov  8 19:58 backup_2026-11-08_19-58-11.sql
-rw-r--r-- 1 root root 2.0K Nov  8 19:59 backup_2026-11-08_19-59-11.sql
-rw-r--r-- 1 root root 2.0K Nov  8 20:00 backup_2026-11-08_20-00-11.sql
-rw-r--r-- 1 root root 2.0K Nov  8 20:01 backup_2026-11-08_20-01-11.sql
INSERT INTO `clientes` VALUES (1,'Ana'),(2,'Bruno'),(3,'Carla');
```
 
### 3. Retenção
 
Com `RETENTION_COUNT=5`, a listagem acima nunca passa de cinco arquivos, mesmo após vários ciclos. Para ver a rotação mais rápido, use `BACKUP_INTERVAL=10` e `RETENTION_COUNT=2` e acompanhe `docker compose logs -f backup`.
 
### 4. Restauração
 
Apague a tabela e restaure a partir do backup mais recente:
 
```bash
docker compose exec db sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" -e "DROP TABLE clientes;"'
docker compose exec backup sh -c 'mysql -h db -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < $(ls -t /backups/*.sql | head -1)'
docker compose exec db sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" -e "SELECT * FROM clientes;"'
```
 
Saída esperada:
 
```text
+----+-------+
| id | nome  |
+----+-------+
|  1 | Ana   |
|  2 | Bruno |
|  3 | Carla |
+----+-------+
```
 
Os avisos sobre a senha que o cliente `mysql` exibe nesses comandos de teste são esperados. O `backup.sh` não gera esses avisos, pois passa a senha pela variável `MYSQL_PWD`.
 
### 5. Persistência
 
```bash
docker compose down
docker compose up -d
docker compose exec backup ls -lh /backups
```
 
Os backups anteriores devem continuar listados, e a tabela `clientes` deve continuar no banco.


## Comandos utilizados
 
- `docker compose up`: constrói e inicia os serviços.
- `docker compose down`: para e remove containers e rede, mantendo os volumes.
- `docker compose logs`: exibe os logs dos containers.
- `docker compose exec`: executa comandos dentro de um container em execução.
- `mysqldump`: gera o arquivo de backup do banco.
- `mysqladmin`: verifica a disponibilidade do servidor MySQL.
- `find`: localiza os arquivos de backup.
- `sort`: ordena os backups pela data no nome do arquivo.
- `rm`: remove os backups antigos.
- `date`: gera data e hora para nomes de arquivos e logs.
- `sleep`: aguarda o intervalo entre os backups.
- `mkdir`: cria o diretório de backups.

## Estrutura do projeto

```text
.
├── backup
│   ├── backup.sh
│   ├── Dockerfile
│   ├── .dockerignore
│   └── entrypoint.sh
├── docker-compose.yml  
├── .env
├── .env.example
├── .gitignore
└── README.md
```

- `backup.sh`: realiza um backup do banco e aplica a retenção.
- `entrypoint.sh`: executa o `backup.sh` periodicamente.
- `Dockerfile`: define a imagem do serviço de backup.
- `.dockerignore`: arquivos que não devem entrar na imagem.
- `docker-compose.yml`: define serviços, volumes, healthcheck e variáveis de ambiente.
- `.env`: configurações e credenciais.
- `.env.example`: modelo das variáveis de ambiente.
- `.gitignore`: impede o envio do `.env` ao repositório.

## Tratamento de erros
 
- Verifica se as variáveis obrigatórias de conexão com o banco foram informadas.
- Verifica se a quantidade de backups é um número inteiro positivo.
- Verifica se o intervalo entre backups é válido.
- Verifica se o banco está disponível antes de iniciar o backup.
- Verifica se o diretório de destino pode ser criado.
- Verifica o código de retorno do `mysqldump` e remove o arquivo incompleto em caso de falha.
- Verifica se a remoção de cada backup antigo foi concluída.

Quando ocorre um erro, a mensagem é exibida nos logs do container. Se o backup falhar, o serviço continua ativo e tenta novamente no próximo intervalo.
 
## Segurança
 
- As credenciais ficam no `.env`, que não é versionado, e não aparecem no código.
- A senha é passada ao cliente MySQL pela variável `MYSQL_PWD`, e não na linha de comando, para não aparecer na lista de processos.
- O serviço de backup usa um usuário comum do banco, e não o root.
- A porta do MySQL não é exposta no host.


