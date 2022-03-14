FROM openjdk:jre-alpine
ARG kafka_version=3.1.0
ARG kafka_filename=kafka_2.13-${kafka_version}
RUN apk update && apk add wget bash dos2unix && rm -rf /var/cache/apk/*
RUN wget https://downloads.apache.org/kafka/${kafka_version}/${kafka_filename}.tgz
RUN tar -xzf ${kafka_filename}.tgz
RUN rm ${kafka_filename}.tgz
WORKDIR ${kafka_filename}
COPY configureAndRun.sh bin/configureAndRun.sh
RUN chmod +x bin/configureAndRun.sh
RUN dos2unix bin/configureAndRun.sh