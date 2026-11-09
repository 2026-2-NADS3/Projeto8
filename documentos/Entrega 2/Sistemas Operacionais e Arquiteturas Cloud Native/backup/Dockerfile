# utiliza Ubuntu como imagem base.
FROM ubuntu:24.04

# instala somente o cliente MySQL e as ferramentas necessárias.
RUN apt-get update && apt-get install -y --no-install-recommends \
	mysql-client-8.0 \
	findutils \
	&& rm -rf /var/lib/apt/lists/*

# define o diretório de trabalho.
WORKDIR /app

# define os scripts para dentro da imagem.
COPY backup.sh entrypoint.sh ./

# concede permissão de execução aos scripts.
RUN chmod +x backup.sh entrypoint.sh

# define o script de inicialização do container.
ENTRYPOINT ["/app/entrypoint.sh"]
