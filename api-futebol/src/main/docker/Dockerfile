# Etapa base: imagem leve para Java 21
FROM eclipse-temurin:21-jdk-jammy AS runner

# Define o diretório de trabalho no container
WORKDIR /work/

# Copia o conteúdo necessário do build para o container
COPY target/quarkus-app/lib/ ./lib/
COPY target/quarkus-app/*.jar ./
COPY target/quarkus-app/app/ ./app/
COPY target/quarkus-app/quarkus/ ./quarkus/

# Expõe a porta padrão do Quarkus (ajuste se necessário)
EXPOSE 8080

# Comando de inicialização do app
CMD ["java", "-jar", "quarkus-run.jar"]
