CREATE STREAM raw_data_stream (
    producedAt VARCHAR KEY
  ) WITH (
    KAFKA_TOPIC = 'raw-data',
    VALUE_FORMAT = 'JSON'
  );

CREATE STREAM REDIS_ENRICHMENT_DATA_STREAM (
    producedAt VARCHAR KEY,
    readAt VARCHAR,
    data BIGINT
  ) WITH (
    KAFKA_TOPIC = 'raw-redis-enrichment-data',
    VALUE_FORMAT = 'JSON'
  );

CREATE STREAM ENRICHED_DATA_STREAM AS
     SELECT 
        rds.producedAt as produced_To_Kafka_At,
        redt.readAt as read_From_Redis_At,
        redt.data as data
     FROM RAW_DATA_STREAM rds
        INNER JOIN REDIS_ENRICHMENT_DATA_STREAM redt WITHIN 1 MINUTES ON rds.producedAt = redt.producedAt;

SHOW TABLES;
SHOW STREAMS;

SELECT * FROM RAW_DATA_STREAM emit changes;
SELECT * FROM REDIS_ENRICHMENT_DATA_STREAM emit changes;
SELECT * FROM ENRICHED_DATA_STREAM emit changes;