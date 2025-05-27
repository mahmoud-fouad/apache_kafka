package com.mfouad.kafkaprod.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {
	
	@Value("${topic.name}")
	String topicName;
	
	@Value("${topic.avro.name}")
	String avroTopicName;
	
	@Value("${topic.partitions}")
	int partitions;
	
	@Value("${topic.wiki.name}")
	String wikiTopicName;
	
	@Value("${topic.wiki.partitions}")
	int wikiPartitions;
	
	@Bean
	public NewTopic firstTopic(){
		
		return  TopicBuilder.name(topicName)
		.partitions(partitions).build();
	}
	
	@Bean
	public NewTopic avroTopic(){
		
		return  TopicBuilder.name(avroTopicName)
				.partitions(partitions).build();
	}
	
	@Bean
	public NewTopic wikiTopic(){
		
		return  TopicBuilder.name(wikiTopicName)
		.partitions(wikiPartitions).build();
	}

}
