public class KafkaConnectEnrichedMessage {
    private String originalMessage;
    private String data;

    public KafkaConnectEnrichedMessage(String originalMessage, String data) {

        this.originalMessage = originalMessage;
        this.data = data;
    }
}