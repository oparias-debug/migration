FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY target/*.jar app.jar
COPY wait-for-keycloak.sh wait-for-keycloak.sh

RUN apt-get update && apt-get install -y curl \
    && chmod +x wait-for-keycloak.sh && apt-get clean

ENTRYPOINT ["./wait-for-keycloak.sh"]
