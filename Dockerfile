# Etapa 1: Construcción
FROM gradle:8.8-jdk21 AS build
LABEL author="Ingsis AHRE"
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre
COPY --from=build /home/gradle/src/build/libs/*.jar /app/snippetRunner.jar
WORKDIR /app
EXPOSE ${PORT}
ENTRYPOINT ["sh", "-c", "java -jar snippetRunner.jar"]
