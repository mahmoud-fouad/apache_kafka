package com.mfouad.kafkacon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;


@Configuration
@EnableKafka
public class KafkaConfig {

	
	 @Bean
	    public ConcurrentKafkaListenerContainerFactory<String, Object> batchKafkaListenerContainerFactory(
	            ConsumerFactory<String, Object> consumerFactory) {
	        
	        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
	            new ConcurrentKafkaListenerContainerFactory<>();
	        
	        // Use the default consumer factory (configured via YAML)
	        factory.setConsumerFactory(consumerFactory);
	        factory.setBatchListener(true); // Enable batch processing
	        
	        // Batch configuration
	        factory.getContainerProperties().setPollTimeout(3000);  // 3 seconds poll timeout
	        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
	        
	        // Concurrency
	        factory.setConcurrency(2); // Number of consumer threads
	        
	        // Error handling for batches
	        factory.setCommonErrorHandler(new DefaultErrorHandler(
	            new FixedBackOff(2000L, 2))); // Retry 2 times with 2 second intervals
	        
	        return factory;
	    }
	
}
