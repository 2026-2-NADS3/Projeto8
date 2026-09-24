#!/bin/bash

# Verifica se foram informados exatamente 3 argumentos.
if [ "${#}" -ne 3 ]; then
    echo "Uso: ./backup.sh <origem> <destino> <quantidade>"
    exit 1
fi

# --- Variáveis ---

# Recebe os argumentos informados na execução do script.
origem="${1}"
destino="${2}"
quantidade="${3}"

# Gera a data e o horário utilizados no nome do arquivo de backup.
data="$(date +"%Y-%m-%d_%H-%M-%S")"

# Define o caminho e o nome do arquivo de backup.
arquivo="${destino}/backup_${data}.tar.gz"

# Verifica se a quantidade informada é um número inteiro positivo.
if ! [[ "${quantidade}" =~ ^[1-9][0-9]*$ ]]; then
    echo "Erro: quantidade deve ser um número inteiro positivo."
    exit 1
fi

# Verifica se o diretório de origem existe.
if [ ! -d "${origem}" ]; then
    echo "Erro: diretório ${origem} não existe."
    exit 1
fi

# Cria o diretório de logs caso ele ainda não exista.
mkdir -p "./logs"

# Armazena o código de retorno do mkdir.
status_log="${?}"

# Verifica se houve erro na criação do diretório de logs.
if [ "${status_log}" -ne 0 ]; then
    echo "Erro: não foi possível criar o diretório de logs."
    exit 1
fi

# Define o arquivo onde os registros das operações serão armazenados.
log_file="./logs/backup.log"

# Cria o diretório de destino caso ele ainda não exista.
mkdir -p "${destino}"

# Armazena o código de retorno do mkdir.
status_mkdir="${?}"

# Verifica se houve erro na criação do diretório de destino.
if [ "${status_mkdir}" -ne 0 ]; then
    echo "Erro: não foi possível criar o diretório de destino"
    echo "[$(date +"%Y-%m-%d %H:%M:%S")] Erro: não foi possível criar o diretório de destino: ${destino}" >> "${log_file}"
    exit 1
fi

# Cria o backup compactado em formato .tar.gz.
tar -czf "${arquivo}" "${origem}"

# Armazena o código de retorno do tar.
status_tar="${?}"

# Verifica se o backup foi criado com sucesso.
if [ "${status_tar}" -eq 0 ]; then
    echo -e "\nBackup criado com sucesso: ${arquivo}."
    echo "[$(date +"%Y-%m-%d %H:%M:%S")] Backup criado com sucesso: ${arquivo}." >> "${log_file}" # Registra o sucesso da operação no arquivo de log.
else
    echo "Erro ao criar backup."
    echo "[$(date +"%Y-%m-%d %H:%M:%S")] Erro ao criar backup." >> "${log_file}" # Registra o erro no arquivo de log.
    rm -f "${arquivo}" # Remove um possível arquivo de backup incompleto.
    exit 1
fi

# Localiza os backups, ordena pela data e seleciona os mais antigos que ultrapassam a quantidade definida.
backup_antigos="$(find "${destino}" -type f -name "backup_*.tar.gz" | sort | head -n "-${quantidade}")"

# Verifica se existem backups antigos para serem removidos.
if [ -n "${backup_antigos}" ]; then
    echo -e "\nRemovendo backups antigos..."
    
    # Percorre a lista de backups antigos, um por vez.
    while IFS= read -r backup_antigo
    do
        rm "${backup_antigo}" # Remove o backup antigo.
        status_rm="${?}" # Armazena o código de retorno do rm.

        # Verifica se a remoção foi realizada com sucesso.
        if [ "${status_rm}" -eq 0 ]; then
            echo "[OK] Backup antigo removido: ${backup_antigo}"
            echo "[$(date +"%Y-%m-%d %H:%M:%S")] Backup antigo removido: ${backup_antigo}" >> "${log_file}" # Registra a remoção no arquivo de log.
        else
            echo "[ERRO] Não foi possível remover: ${backup_antigo}"
            echo "[$(date +"%Y-%m-%d %H:%M:%S")] Erro ao remover backup: ${backup_antigo}" >> "${log_file}" # Registra o erro no arquivo de log.
        fi
    done <<< "${backup_antigos}"
fi

# Indica que todas as operações foram concluídas.
echo -e "\nOperações concluídas."
