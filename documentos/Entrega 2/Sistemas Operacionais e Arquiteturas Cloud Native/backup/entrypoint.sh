#!/bin/bash

# Define o intervalo entre os backups.
intervalo="${BACKUP_INTERVAL:-3600}"

# Verifica se o intervalo é um número positivo.
if ! [[ "${intervalo}" =~ ^[1-9][0-9]*$ ]]; then
    echo "Erro: intervalo de backup inválido."
    exit 1
fi

# Encerra o servilo de forma limpa ao receber SIGTERN ou SIGINT
trap 'echo "Encerrando serviço de backup..."; exit 0' SIGTERM SIGINT

echo "Iniciando serviço de backup..."
echo "Intervalo entre backups: ${intervalo} segundos."

# Executa os backups periodicamente.
while true; do

    echo "Iniciando nova execução..."

    /app/backup.sh

    echo "Próxima execução em ${intervalo} segundos."

    # O sleep roda em segundo plano e o wait aguarda por ele,
    # fazendo o trap executar instantaneamente.
    sleep "${intervalo}" &
    wait $!

done
