import com.google.gson.internal.Streams;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

public class EnrichmentKafkaStreamsApplication {
    public static void main(final String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "EnrichmentKafkaStreamsApplication");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> rawDataStream = streamsBuilder.stream("raw-data");
        rawDataStream.foreach((key, value) -> System.out.println("[" + key + "," + value + "]"));

        KStream<String, String> rawEnrichmentDataStream = streamsBuilder.stream("raw-redis-enrichment-data");
        rawEnrichmentDataStream.foreach((key, value) -> System.out.println("[" + key + "," + value + "]"));

        KStream<String, String> enrichedRawData = rawDataStream.join(rawEnrichmentDataStream,
                (readOnlyKey, rawDataValue, rawEnrichmentDataValue) -> rawDataValue + ":" + rawEnrichmentDataValue,
                JoinWindows.of(Duration.ofMinutes(1)));
        enrichedRawData.foreach((key, value) -> System.out.println("[" + key + "," + value + "]"));

        Topology topology = streamsBuilder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
        }));
    }

}
