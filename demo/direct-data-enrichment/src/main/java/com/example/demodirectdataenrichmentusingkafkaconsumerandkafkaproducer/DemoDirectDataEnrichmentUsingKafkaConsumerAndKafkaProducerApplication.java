package com.example.demodirectdataenrichmentusingkafkaconsumerandkafkaproducer;

import com.google.gson.Gson;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DemoDirectDataEnrichmentUsingKafkaConsumerAndKafkaProducerApplication {

    private final KafkaTemplate<String, String> _template;

    RedissonClient redisson;

    public static void main(String[] args) {
        SpringApplication.run(DemoDirectDataEnrichmentUsingKafkaConsumerAndKafkaProducerApplication.class, args);
    }

    public DemoDirectDataEnrichmentUsingKafkaConsumerAndKafkaProducerApplication(KafkaTemplate<String, String> template) {
        this._template = template;
    }

    @PostConstruct
    private void initialize() throws Exception {
        try{
            Config config = new Config();
            String redisConnectionString = "redis://redis:6379";
            config.useSingleServer().setAddress(redisConnectionString);
            this.redisson = Redisson.create(config);
            System.out.println("Connected To Redis");
        } catch (Exception ex){
            System.out.println("Error Connecting To Redis" + ex.getMessage());
            throw new Exception(ex.getMessage());
        }
    }

    @KafkaListener(topics = "raw-data")
    public void enrichAndProduceEnrichedMessage(String message) {
        //Read enrichment data from Redis
        RAtomicLong atomicLong = redisson.getAtomicLong("tokenId");

        DirectlyEnrichedMessage directlyEnrichedMessage
                = new DirectlyEnrichedMessage(message, String.valueOf(atomicLong.get()));

        Gson gson = new Gson();
        String recordJsonValue = gson.toJson(directlyEnrichedMessage);

        //Produce enriched message
        _template.send("enriched-using-direct-kafka-consumer-and-kafka-producer", recordJsonValue);
    }
}
