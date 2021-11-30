package com.example.demorawdatakafkaproducer;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@EnableScheduling()
@SpringBootApplication
public class DemoRawDataKafkaProducerApplication {

    private final KafkaTemplate<String, String> _template;

    public static void main(String[] args) {
        SpringApplication.run(DemoRawDataKafkaProducerApplication.class, args);
    }

    public DemoRawDataKafkaProducerApplication(KafkaTemplate<String, String> template) {
        this._template = template;
    }

    @Scheduled(fixedRate=1000)
    private void scheduledMessageProducer() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

        Gson gson = new Gson();
        String recordJsonValue = gson.toJson(new RawDataMessage(dateFormat.format(new Date())));

        // Producing the same value both as key and value
        _template.send("raw-data", recordJsonValue, recordJsonValue);
    }
}
