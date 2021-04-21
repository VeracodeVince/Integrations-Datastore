FROM openjdk:11-jre-slim

ARG COMMIT_SHA
LABEL cx.commit-sha ${COMMIT_SHA}

WORKDIR app
EXPOSE 8080
RUN apt update && \
    apt upgrade -y && \
    apt install curl -y
COPY target/cx-integrations-datastore-*.jar cx-integrations-datastore.jar
HEALTHCHECK CMD curl http://localhost:8080/actuator/health

ENTRYPOINT ["java", "-Xms512m", "-Xmx2048m", "-jar", "cx-integrations-datastore.jar"]
