package com.example.demoenrichmentdataredisproducer;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

@EnableScheduling
@SpringBootApplication
public class DemoEnrichmentDataRedisProducerApplication {

    RedissonClient redisson;

    public static void main(String[] args) {
        SpringApplication.run(DemoEnrichmentDataRedisProducerApplication.class, args);
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

    @Scheduled(fixedRate=1000)
    private void scheduledEnrichmentProducer(){
        RAtomicLong atomicLong = redisson.getAtomicLong("tokenId");
        atomicLong.set(atomicLong.get() + 1);
    }
}
