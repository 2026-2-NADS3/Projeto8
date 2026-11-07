# Passo - App Mobile (Programação Mobile)

App Android em Java com 4 abas deslizáveis (Início, Agenda, Mural, Perfil), login e cadastro com MySQL, leitor de QR Code e telas de erro.

O banco e a API rodam **localmente** (não estão online). Para testar o login, siga os passos abaixo no Windows.

## Conteúdo

- `app-debug.apk`: app pronto para instalar
- `Etapp_02/`: projeto do Android Studio (Java)
- `api/`: API em Node.js que liga o app ao MySQL
- `banco.sql`: script que cria o banco

## 1. Instalar os programas

1. **Node.js (versão LTS):** https://nodejs.org (instalar com "Next" até o fim)
2. **MySQL:** https://dev.mysql.com/downloads/installer/
   - Escolha **Server only**
   - Quando pedir a senha do `root`, crie uma e anote
   - Deixe as demais opções padrão

## 2. Criar o banco

1. Abra o menu Iniciar e procure **MySQL 8.x Command Line Client**
2. Digite a senha do `root`
3. Rode o comando abaixo, trocando o caminho pelo da pasta onde está o `banco.sql` (use `/` em vez de `\`):

```
source C:/Users/SEU_USUARIO/Downloads/banco.sql
```

Deve aparecer "Query OK" várias vezes. Pode fechar a janela.

## 3. Ligar a API

1. Abra a pasta `api`, clique na barra de endereço do Explorer, digite `cmd` e Enter
2. Rode:

```
npm install
node server.js
```

3. Deve aparecer `API rodando na porta 3000`. **Deixe essa janela aberta** durante o teste.
4. Se o Windows perguntar sobre o Firewall, clique em **Permitir acesso**.

## 4. Rodar o app

**Opção A: APK no emulador**
1. Abra o Android Studio e inicie um emulador (Device Manager)
2. Arraste o arquivo `app-debug.apk` para dentro da janela do emulador

**Opção B: pelo projeto**
1. Abra a pasta `Etapp_02` no Android Studio e aguarde o Gradle sincronizar
2. Escolha um emulador e clique em Run (▶)

O app usa o endereço `10.0.2.2:3000`, que é como o emulador enxerga a API do computador.

## 5. Como testar

1. Na tela de login, toque em **Cadastre-se**, preencha e crie a conta
2. Volte para **Entrar** e faça login (marque "Manter-se conectado" para ficar logado)
3. Deslize para os lados ou toque no menu de baixo para trocar de aba
4. Agenda: botões Vou / Não vou e Registrar presença (leitor de QR Code)
5. Mural: filtros e "+ Agenda" (as escolhas ficam salvas ao reabrir o app)
6. Para ver a tela "Sem internet", feche a janela da API e tente entrar

## Funcionalidades

- Login e cadastro com MySQL (senha guardada com hash bcrypt)
- "Manter-se conectado", filtros, agenda e presença salvos no aparelho
- Leitor de QR Code (link abre no navegador, texto confirma presença)
- Telas de erro: sem internet, algo deu errado, em construção, sem novidades, presença confirmada
- Telas ainda em construção: Meu card, certificados, testes, ajuda e configurações

## Problemas comuns

- **"Sem internet" no app:** a API não está rodando (passo 3) ou o MySQL não foi criado (passo 2)
- **Erro `Access denied` na API:** rode o `banco.sql` de novo
- **Celular físico:** o APK só funciona no emulador. Para celular, troque `URL_BASE` em `LoginActivity.java` pelo IP do PC (`ipconfig`), gere o APK de novo e use o mesmo Wi-Fi.
