package com.mfouad.kafkaprod.config;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaMultipleProducerConfig {

	// TODO Auto-generated constructor stub
	private final KafkaCustomProperties kafkaCustomProperties;

	@Bean
	@Qualifier("producer1")
	public KafkaTemplate<String, Object> producer1KafkaTemplate() {
		return new KafkaTemplate<>(producerFactory("producer1"));
	}

	@Bean
	@Qualifier("producer2")
	public KafkaTemplate<String, Object> producer2KafkaTemplate() {
		return new KafkaTemplate<>(producerFactory("producer2"));
	}

	 private ProducerFactory<String, Object> producerFactory(String producerName) {
	        Map<String, Object> properties = new HashMap<>(kafkaCustomProperties.buildCommonProperties());
	        if (kafkaCustomProperties.getProducer() != null) {
	            KafkaProperties.Producer producerProperties = kafkaCustomProperties.getProducer().get(producerName);
	            if (producerProperties != null) {
	            	log.info("in KafkaMultipleProducerConfig for producer {} get getKeySerializer ",producerName,producerProperties.getKeySerializer().getName());
	            	log.info("in KafkaMultipleProducerConfig for producer {} get properties ",producerName,producerProperties.getProperties().get(""));
	                properties.putAll(producerProperties.buildProperties(null));
	            }
	        }
	        return new DefaultKafkaProducerFactory<>(properties);
	    }

}
