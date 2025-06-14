package wordCount;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

public class WordCountApp {

	public static void main(String[] args) {
		Properties props = getConfig();

		StreamsBuilder streamsBuilder = new StreamsBuilder();
		// Build the Topology
		/**
		 * * This topology reads from the "sentences" topic, splits each sentence into
		 * words, groups by word, counts the occurrences of each word, and writes the
		 * results to the "word-count" topic.
		 */
		
		streamsBuilder.<String, String>stream("sentences")
				.flatMapValues((key, value) -> Arrays.asList(value.toLowerCase().split(" ")))
				.groupBy((key, value) -> value).count(Materialized.with(Serdes.String(), Serdes.Long())).toStream()
				.to("word-count", Produced.with(Serdes.String(), Serdes.Long()));
		
				
		
		// Create the Kafka Streams Application
		KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), props);
		// Start the application
		kafkaStreams.start();

		// attach shutdown handler to catch control-c
		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
	}

	private static Properties getConfig() {
		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
		return properties;
	}

}
