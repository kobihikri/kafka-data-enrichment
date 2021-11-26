import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisEnrichmentDataFetcherKafkaSinkConnector extends SinkConnector {

    private String redisHostName;
    private String redisPort;
    private String outputTopicName;

    static final String REDIS_HOST_NAME = "redis.host.name";
    static final String REDIS_PORT = "redis.port";
    static final String OUTPUT_TOPIC_NAME = "output.topic.name";

    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(REDIS_HOST_NAME, ConfigDef.Type.STRING, null,
                    ConfigDef.Importance.HIGH, "A Redis cluster host name")
            .define(REDIS_PORT, ConfigDef.Type.INT, null,
                    ConfigDef.Importance.HIGH, "A Redis cluster port number")
            .define(OUTPUT_TOPIC_NAME, ConfigDef.Type.STRING, null,
                    ConfigDef.Importance.HIGH, "A kafka topic name for the output");

    @Override
    public void start(Map<String, String> props) {
        System.out.println("RedisEnrichmentDataFetcherKafkaSinkConnector -> start -> invoked [" + props + "]");
        redisHostName = props.get(REDIS_HOST_NAME);
        redisPort = props.get(REDIS_PORT);
        outputTopicName = props.get(OUTPUT_TOPIC_NAME);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return RedisEnrichmentDataFetcherKafkaSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {

        // Here we can split our work between connector tasks, according to our configuration
        // Example: In case we are listening to multiple Redis clusters using a single connector instance,
        // we could have created a single config map object per each Redis cluster connection string, which would have
        // looked like this (pseudo code):
        // -------------------------------
        // for each redisConnectionString in REDIS_CONNECTION_STRINGS
        //      currentTaskConfig = new config(redisConnectionString, outputTopicName)
        //      configs.add(currentTaskConfig)
        // return configs
        // -------------------------------

        ArrayList<Map<String, String>> configs = new ArrayList<>();
        Map<String, String> config = new HashMap<>();
        config.put(REDIS_HOST_NAME, redisHostName);
        config.put(REDIS_PORT, redisPort);
        config.put(OUTPUT_TOPIC_NAME, outputTopicName);
        configs.add(config);
        return configs;
    }

    @Override
    public void stop() {
        System.out.println("RedisEnrichmentDataFetcherKafkaSinkConnector -> stop -> invoked");
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}
