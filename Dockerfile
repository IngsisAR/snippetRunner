# Etapa 1: Construcción
FROM gradle:8.8-jdk21 AS build

LABEL author="Ingsis AR"

COPY . /home/gradle/src

WORKDIR /home/gradle/src

# Necesario para las actions de docker publish
ARG USERNAME
ENV USERNAME=${USERNAME}

ARG TOKEN
ENV TOKEN=${TOKEN}

RUN gradle build --no-daemon

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre

COPY --from=build /home/gradle/src/build/libs/*.jar /app/snippetRunner.jar
COPY --from=build /home/gradle/src/newrelic/newrelic.jar /newrelic.jar
COPY --from=build /home/gradle/src/newrelic/newrelic.yml /newrelic.yml
COPY src/main/resources/static /app/src/main/resources/static

WORKDIR /app
EXPOSE ${PORT}

ENTRYPOINT ["java", "-javaagent:/newrelic.jar", "-Dnewrelic.config.license_key=${NEW_RELIC_LICENSE_KEY}", "-Dnewlic.config.app_name=${NEW_RELIC_APP_NAME}", "-jar", "/app/snippetRunner.jar"]
