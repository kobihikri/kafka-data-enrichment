#!/usr/bin/env bash
echo "configureAndRun -> Configuring container"

echo "ZOOKEEPER_ENABLED="       ${ZOOKEEPER_ENABLED}
echo "KAFKA_ENABLED="           ${KAFKA_ENABLED}
echo "KAFKA_CONNECT_ENABLED="   ${KAFKA_CONNECT_ENABLED}

if [[ ${ZOOKEEPER_ENABLED} == 'true' ]]; then
    echo "configureAndRun -> Configuring Zookeeper"
    echo dataLogDir =   ${ZOOKEEPER_LOG_DIR}    >> config/zookeeper.properties
    echo dataDir    =   ${ZOOKEEPER_DATA_DIR}   >> config/zookeeper.properties
    echo clientPort =   ${ZOOKEEPER_PORT}       >> config/zookeeper.properties
    echo "configureAndRun -> Zookeeper Instance Configured"
    bin/zookeeper-server-start.sh ${ZOOKEEPER_INVOCATION_ARGS} config/zookeeper.properties
fi

if [[ ${KAFKA_ENABLED} == 'true' ]]; then
    echo "configureAndRun -> Configuring Kafka"
    echo broker.id                        = ${KAFKA_BROKER_ID}                          >> config/server.properties
    echo log.dir                          = ${KAFKA_LOG_DIR}                            >> config/server.properties
    echo log.dirs                         = ${KAFKA_DATA_DIR}                           >> config/server.properties
    echo zookeeper.connect                = ${KAFKA_ZOOKEEPER_CONNECT}                  >> config/server.properties
    echo offsets.topic.replication.factor = ${KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR}   >> config/server.properties
    echo listeners                        = ${KAFKA_LISTENERS}                          >> config/server.properties
    echo num.partitions                   = ${KAFKA_NUM_PARTITIONS}                     >> config/server.properties
    echo "configureAndRun -> Kafka Configured"
    bin/kafka-server-start.sh ${KAFKA_INVOCATION_ARGS} config/server.properties
fi

if [[ ${KAFKA_CONNECT_ENABLED} == 'true' ]]; then
    echo "configureAndRun -> Configuring Kafka Connect"
    echo dataLogDir         = ${KAFKA_CONNECT_LOG_DIR}              >> config/connect-distributed.properties
    echo dataDir            = ${KAFKA_CONNECT_DATA_DIR}             >> config/connect-distributed.properties
    echo bootstrap.servers  = ${KAFKA_CONNECT_BOOTSTRAP_SERVERS}    >> config/connect-distributed.properties
    echo group.id           = ${KAFKA_CONNECT_GROUP_ID}             >> config/connect-distributed.properties
    echo key.converter      = ${KAFKA_CONNECT_KEY_CONVERTER}        >> config/connect-distributed.properties
    echo value.converter    = ${KAFKA_CONNECT_VALUE_CONVERTER}      >> config/connect-distributed.properties
    echo plugin.path        = ${KAFKA_CONNECT_PLUGIN_PATH}          >> config/connect-distributed.properties
    echo "configureAndRun -> Kafka Connect Configured"
    bin/connect-distributed.sh ${KAFKA_CONNECT_INVOCATION_ARGS} config/connect-distributed.properties
fi