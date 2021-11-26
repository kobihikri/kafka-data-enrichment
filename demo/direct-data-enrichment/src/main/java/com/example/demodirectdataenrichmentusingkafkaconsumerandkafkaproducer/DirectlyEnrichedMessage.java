package com.example.demodirectdataenrichmentusingkafkaconsumerandkafkaproducer;

public class DirectlyEnrichedMessage {
    private String originalMessage;
    private String data;

    public DirectlyEnrichedMessage(String originalMessage, String data) {

        this.originalMessage = originalMessage;
        this.data = data;
    }
}
