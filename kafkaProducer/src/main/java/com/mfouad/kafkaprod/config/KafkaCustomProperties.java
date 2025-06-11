package com.mfouad.kafkaprod.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Getter
@Setter
public class KafkaCustomProperties {

	
	 private List<String> bootstrapServers = new ArrayList<>(Collections.singletonList("localhost:9092"));
	    private String clientId;
	    private Map<String, String> properties = new HashMap<>();
	    private Map<String, KafkaProperties.Producer> producer;
	    private Map<String, KafkaProperties.Consumer> consumer;
	    private KafkaProperties.Ssl ssl = new KafkaProperties.Ssl();
	    private KafkaProperties.Security security = new KafkaProperties.Security();

	    public Map<String, Object> buildCommonProperties() {
	        Map<String, Object> properties = new HashMap<>();
	        if (this.bootstrapServers != null) {
	            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
	        }
	        properties.putAll(this.ssl.buildProperties());
	        properties.putAll(this.security.buildProperties());
	        if (!CollectionUtils.isEmpty(this.properties)) {
	            properties.putAll(this.properties);
	        }
	        return properties;
	    }	
	
}
