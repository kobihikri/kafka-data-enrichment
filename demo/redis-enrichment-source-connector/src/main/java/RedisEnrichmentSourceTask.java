import com.google.gson.Gson;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class RedisEnrichmentSourceTask extends SourceTask {

    private String redisHostName;
    private String redisPort;
    private String outputTopicName;

    Map sourcePartition;

    //Will be used as a client for retrieving enrichment data from Redis
    RedissonClient redisson;

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }

    @Override
    public void start(Map<String, String> props) {
        System.out.println("RedisEnrichmentSourceTask -> start -> invoked [" + props + "]");
        redisHostName = props.get(RedisEnrichmentSourceConnector.REDIS_HOST_NAME);
        redisPort = props.get(RedisEnrichmentSourceConnector.REDIS_PORT);

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

        outputTopicName = props.get(RedisEnrichmentSourceConnector.OUTPUT_TOPIC_NAME);
        sourcePartition = Collections.singletonMap("redisHostName", redisHostName);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        System.out.println("RedisEnrichmentDataFetcherSourceTask -> poll -> invoked");
        List<SourceRecord> result = new ArrayList<>();

        RAtomicLong atomicLong = redisson.getAtomicLong("tokenId");

        final String localDateTime = LocalDateTime.now().toString();
        final Map sourceOffset = Collections.singletonMap("tokenOffset", localDateTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String formattedDate = dateFormat.format(new Date());

        Gson gson = new Gson();
        String recordJsonValue = gson.toJson(new RedisEnrichmentDataMessage(formattedDate, String.valueOf(atomicLong.get())));
        String rawEnrichmentMessageKey = gson.toJson(new RawEnrichmentMessageKey(formattedDate));

        final SourceRecord record = new SourceRecord(sourcePartition, sourceOffset,
                outputTopicName, Schema.STRING_SCHEMA, rawEnrichmentMessageKey,
                Schema.STRING_SCHEMA, recordJsonValue);

        result.add(record);

        // For Demo Sake - Don't poll too often
        Thread.sleep(1000);

        return result;
    }

    @Override
    public void stop() {
        System.out.println("RedisEnrichmentDataFetcherSourceTask -> stop -> invoked");
    }
}
