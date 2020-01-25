FROM openjdk:8-jre-alpine

EXPOSE 7000
ENTRYPOINT ["/usr/bin/java", "-jar", "/opt/meerkat/meerkat.jar"]

ARG JAR_FILE
ADD target/meerkat-app.jar /opt/meerkat/meerkat.jar