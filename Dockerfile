FROM openjdk:11-jre-slim

WORKDIR app
EXPOSE 80
COPY target/cx-integrations-datastore-*.jar cx-integrations-datastore.jar
ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "cx-integrations-datastore.jar"]
