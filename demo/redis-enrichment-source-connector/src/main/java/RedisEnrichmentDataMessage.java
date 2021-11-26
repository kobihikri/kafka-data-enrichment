public class RedisEnrichmentDataMessage {
    private String readAt;
    private String data;

    public RedisEnrichmentDataMessage(String readAt, String data) {

        this.readAt = readAt;
        this.data = data;
    }
}