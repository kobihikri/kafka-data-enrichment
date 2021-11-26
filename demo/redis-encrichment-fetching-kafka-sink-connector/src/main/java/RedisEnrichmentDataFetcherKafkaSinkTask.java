import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.net.ConnectException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class RedisEnrichmentDataFetcherKafkaSinkTask extends SinkTask {

    private String redisHostName;
    private String redisPort;
    private String outputTopicName;

    Map sourcePartition;

    //Will be used as a client for retrieving enrichment data from Redis
    RedissonClient redisson;

    //Will be used as a client for writing enriched messages to a kafka topic
    Producer<String, String> producer;


    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        System.out.println("RedisEnrichmentDataFetcherKafkaSinkTask -> start -> invoked [" + props + "]");
        redisHostName = props.get(RedisEnrichmentDataFetcherKafkaSinkConnector.REDIS_HOST_NAME);
        redisPort = props.get(RedisEnrichmentDataFetcherKafkaSinkConnector.REDIS_PORT);

        try{
            Config config = new Config();
            String redisConnectionString = "redis://" + redisHostName + ":" + redisPort;
            System.out.println("RedisEnrichmentDataFetcherSourceTask -> start -> [redisConnectionString = "
                    + redisConnectionString + "]");
            config.useSingleServer().setAddress(redisConnectionString);
            redisson = Redisson.create(config);
        } catch (Exception ex){
            System.out.println("RedisEnrichmentDataFetcherSourceTask -> start -> exception encountered:"
                    + ex.getMessage());
        }

        outputTopicName = props.get(RedisEnrichmentDataFetcherKafkaSinkConnector.OUTPUT_TOPIC_NAME);
        sourcePartition = Collections.singletonMap("redisHostName", redisHostName);

        Properties producerConfig = new Properties();
        producerConfig.put("bootstrap.servers", "kafka:9092");
        producerConfig.put("acks", "all");
        producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(producerConfig);
    }

    @Override
    public void put(Collection<SinkRecord> recordsToEnrichWithRedisData) {
        System.out.println("RedisEnrichmentDataFetcherKafkaSinkTask -> put -> invoked");

        //DATA ENRICHMENT CODE
        //--------------------
        //Per each record we've read from kafka - we would like to activate our enrichment function and write the source
        //record together with the enrichment function result. In our case, we are attaching a token read from Redis
        // to the original message - and writing back to an output kafka topic.

        recordsToEnrichWithRedisData.forEach(sinkRecord -> {
            //Read enrichment data from Redis
            RAtomicLong atomicLong = redisson.getAtomicLong("tokenId");
            KafkaConnectEnrichedMessage directlyEnrichedMessage
                    = new KafkaConnectEnrichedMessage(sinkRecord.value().toString(), String.valueOf(atomicLong.get()));

            Gson gson = new Gson();
            String recordJsonValue = gson.toJson(directlyEnrichedMessage);

            //Produce enriched message
            producer.send(new ProducerRecord<>(outputTopicName, recordJsonValue));
        });
    }

    @Override
    public void stop() {
        System.out.println("RedisEnrichmentDataFetcherKafkaSinkTask -> stop -> invoked");
        producer.close();
    }
}
