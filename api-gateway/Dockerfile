FROM quay-quay-quay.apps.ocp-hub-3t.mh.gob.sv/mh-usi-ubi/openjdk-21-runtime-mh:21

USER root

WORKDIR /app

COPY target/app.jar app.jar

EXPOSE 8080

USER 185

ENTRYPOINT ["java", "-jar", "app.jar"]
