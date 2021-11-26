FROM openjdk:11
ARG JAR_FILE=target/*with-dependencies.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-cp", "*", "EnrichmentKafkaStreamsApplication"]