# Etapa 1: build da aplicação
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagem final enxuta
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Porta do Ranking Service (o Core usa 8080)
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
