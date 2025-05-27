package com.mfouad.kafkacon;


import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.mfouad.kafkaprod.MessageDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Consumer {
	
	/*@KafkaListener(topics={"${topic.name}"},  groupId="mygroup"
			,topicPartitions = {@TopicPartition(topic ="${topic.name}" , partitions = {"1"} )})
	public void consume1(MessageDTO message){
		
		log.info("in consume1 {} --------------------");
		log.info("receive {}",message);
		checkMessage(message);
		
	}
	
	@KafkaListener(topics={"${topic.name}"},  groupId="mygroup"
			,topicPartitions = {@TopicPartition(topic ="${topic.name}" , partitions = {"0"} )})
	public void consume2(MessageDTO message){
		
		log.info("in consume2 {} --------------------");
		log.info("receive {}",message);
		
		checkMessage(message);
		
	}*/
	
	private void checkMessage(MessageDTO msg) {
		if("exit".equals (msg.getMessage() ) ) {
			throw new RuntimeException("exit not permiteted");
		}
	}
	
	@RetryableTopic(attempts = "3",exclude = {NullPointerException.class}) //retry  N-1 => retry twice
	// don't retry if NullPointerException been raised
	@KafkaListener(topics={"${topic.name}"},  groupId="mygroup")
	public void consume3(MessageDTO message){
		
		log.info("in consume3 {} --------------------");
		log.info("receive {}",message);
		checkMessage(message);
		
	}
	
	@DltHandler
	public void ListenonDLT(MessageDTO msg ,@Header( KafkaHeaders.RECEIVED_TOPIC) String topic,@Header( KafkaHeaders.OFFSET) int offset ) {
		log.info("DLT recived {} from topic {} with offest {}",msg.getMessage(),topic,offset);
	}
	

}
