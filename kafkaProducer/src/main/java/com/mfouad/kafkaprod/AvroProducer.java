package com.mfouad.kafkaprod;


import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AvroProducer {
	
	 @Autowired
	 @Qualifier("producer2")
	private KafkaTemplate<String, Object> template;
	
	@Value("${topic.avro.name}")
	String topicName;
	
	
	public void sendStringMessage(String name){
		
		log.info("----------- send messag {} to topic {}",name,topicName);
		
		Message<Employee> sendMessage = MessageBuilder.withPayload(new Employee(1, name, name))
		.setHeader(KafkaHeaders.TOPIC, topicName)
		.build();
		 CompletableFuture<SendResult<String, Object>> messageinQueue = template.send(sendMessage);
		
		
		messageinQueue.whenComplete((mssg,ex) -> {
			if(ex==null) {
				
			log.info("send message from employee {} on partion {} with offset {} ",((Employee)(mssg.getProducerRecord().value())).getFirstName()
					,mssg.getRecordMetadata().partition(),mssg.getRecordMetadata().offset()
					);
			}
			
			
		});
		
	}

}
