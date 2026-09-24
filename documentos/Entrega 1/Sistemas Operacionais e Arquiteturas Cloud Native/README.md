# Sistema de Backup Automatizado em Bash

## Descrição

Este projeto consiste em um script Bash para automatizar o processo de backup de um diretório 
no Linux. O script cria arquivos de backup compactados no formato `.tar.gz`, armazena os 
backups em um diretório definido pelo usuário e remove automaticamente os backups mais 
antigos, mantendo apenas a quantidade especificada.

## Funcionamento

O script realiza as seguintes etapas:

1. Valida os parâmetros informados pelo usuário.
2. Verifica se a quantidade de backups é um número inteiro positivo.
3. Verifica se o diretório de origem existe.
4. Cria os diretórios de destino e de logs, caso necessário.
5. Cria o backup compactado no formato `.tar.gz`.
6. Registra o resultado da operação no arquivo de log.
7. Localiza os backups antigos e remove aqueles que ultrapassam a quantidade definida.
8. Registra a remoção dos backups antigos no arquivo de log.

## Requisitos

- Linux
- Bash
- `tar`
- `find`
- `sort`
- `head`
- `rm`
- `date`
- `mkdir`

## Execução

Primeiro, dê permissão ao script:
```bash
chmod +x backup.sh
```

Depois, execute o script informando:
```bash
./backup.sh <origem> <destino> <quantidade>
```

Onde:
- `<origem>` é o diretório que será utilizado para o backup;
- `<destino>` é o diretório onde os backups serão armazenados;
- `<quantidade>` é a quantidade de backups mais recentes que devem ser mantidos.

## Exemplo de uso

Para realizar o backup do diretório `Documentos`, armazenar em `backups` e manter os três 
backups mais recentes:
```bash
./backup.sh ./Documentos ./backups 3
```

Saída:

```text
Backup criado com sucesso: ./backups/backup_2026-09-23_12-42-54.tar.gz.

Removendo backups antigos...
[OK] Backup antigo removido: ./backups/backup_2026-09-23_12-41-29.tar.gz

Operações concluídas.
```

## Logs

As operações realizadas pelo script são registradas no arquivo `logs/backup.log`.
O arquivo registra a data e o horário da operação, além do resultado da ação realizada.

Exemplo:

```text
[2026-09-23 12:42:54] Backup criado com sucesso: ./backups/backup_2026-09-23_12-42-54.tar.gz.
[2026-09-23 12:42:54] Backup antigo removido: ./backups/backup_2026-09-23_12-41-29.tar.gz
```

## Retenção de backups

O terceiro parâmetro informado na execução define quantos backups mais recentes serão mantidos.
Por exemplo:

```bash
./backup.sh ./Documentos ./backups 3
```

Nesse caso, o script mantém os três backups mais recentes e remove os backups anteriores.

## Comandos utilizados

- `tar`: cria o arquivo compactado do backup no formato `.tar.gz`.
- `find`: localiza os arquivos de backup existentes no diretório de destino.
- `sort`: ordena os backups pela data presente no nome dos arquivos.
- `head`: identifica os backups mais antigos que devem ser removidos.
- `rm`: remove os backups antigos.
- `date`: gera a data e o horário utilizados nos nomes dos arquivos e nos logs.
- `mkdir`: cria os diretórios necessários para armazenar os backups e os logs.

## Estrutura do projeto
```text
.
├─ backup.sh
└─ README.md
```

`backup.sh`: script responsável pela execução dos backups.
`README.md`: documentação do projeto.

## Tratamento de erros

O script realiza algumas verificações para evitar falhas durante sua execução.

- Verifica se foram informados os três parâmetros necessários.
- Verifica se a quantidade de backups é um número inteiro positivo.
- Verifica se o diretório de origem existe.
- Verifica se os diretórios de destino e de logs podem ser criados.
- Verifica o código de retorno do comando `tar`.
- Verifica se a remoção dos backups antigos foi realizada com sucesso.

Quando ocorre um erro, uma mensagem é exibida no terminal e a ocorrência é registrada no 
arquivo `logs/backup.log` quando aplicável.
