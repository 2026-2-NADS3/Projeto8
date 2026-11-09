#!/bin/bash

# Verifica se as variáveis obrigatórias foram informadas.
if [ -z "${MYSQL_HOST}" ] || [ -z "${MYSQL_USER}" ] || \
   [ -z "${MYSQL_PASSWORD}" ] || [ -z "${MYSQL_DATABASE}" ]; then

    echo "Erro: configurações do banco não informadas."
    exit 1
fi

export MYSQL_PWD="${MYSQL_PASSWORD}"

# Define as variáveis do backup.
destino="${BACKUP_DIR:-/backups}"
quantidade="${RETENTION_COUNT:-5}"

# Verifica se a quantidade é um número inteiro positivo.
if ! [[ "${quantidade}" =~ ^[1-9][0-9]*$ ]]; then
    echo "Erro: quantidade de backups inválida."
    exit 1
fi

# Cria o diretório de destino.
mkdir -p "${destino}"

if [ "${?}" -ne 0 ]; then
    echo "Erro ao criar diretório de backups."
    exit 1
fi

# Define a data e o nome do arquivo.
data="$(date +"%Y-%m-%d_%H-%M-%S")"
arquivo="${destino}/backup_${data}.sql"

# Define a função de log.
log() {
    echo "[$(date +"%Y-%m-%d %H:%M:%S")] $1"
}

# Verifica se o MySQL está disponível.
if ! mysqladmin ping -h "${MYSQL_HOST}" \
    -u "${MYSQL_USER}" \
    --silent; then

    log "Erro: banco de dados indisponível."
    exit 1
fi

# Realiza o backup do banco de dados.
if mysqldump -h "${MYSQL_HOST}" \
    -u "${MYSQL_USER}" \
    --single-transaction \
    --routines \
    --triggers \
    --no-tablespaces \
    "${MYSQL_DATABASE}" > "${arquivo}"; then

    log "Backup criado com sucesso: ${arquivo}"

else

    log "Erro ao realizar backup."
    rm -f "${arquivo}"
    exit 1

fi

# Localiza os backups e ordena do mais recente para o mais antigo.
mapfile -t backups < <(
    find "${destino}" -maxdepth 1 -type f \
    -name "backup_*.sql" | sort -r
)

# Remove os backups que ultrapassam a quantidade permitida.
for ((i=quantidade; i<${#backups[@]}; i++)); do

    backup_antigo="${backups[$i]}"

    if rm -f "${backup_antigo}"; then
        log "Backup antigo removido: ${backup_antigo}"
    else
        log "Erro ao remover backup: ${backup_antigo}"
    fi

done

log "Operação de backup concluída."
