FROM kafka-common:latest
ENV ZOOKEEPER_ENABLED=false
ENV KAFKA_ENABLED=false
ENV KAFKA_CONNECT_ENABLED=true
COPY compiled-connectors bin/compiled-connectors
RUN chmod +x bin/configureAndRun.sh
ENTRYPOINT ["bash", "bin/configureAndRun.sh"]