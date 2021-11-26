FROM kafka-common:latest
ENV ZOOKEEPER_ENABLED=true
ENV KAFKA_ENABLED=false
ENV KAFKA_CONNECT_ENABLED=false
RUN chmod +x bin/configureAndRun.sh
ENTRYPOINT ["bin/configureAndRun.sh"]