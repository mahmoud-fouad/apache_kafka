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
public class Producer {
	
	@Qualifier("producer1")
	 @Autowired
	private KafkaTemplate<String, Object> template;
	
	@Value("${topic.name}")
	String topicName;
	
	
	public void sendStringMessage(String message,String... topicName){
		String targetTopic =null;
		
		if (topicName.length > 0) {
			targetTopic = topicName[0];
		}
		else
			targetTopic = this.topicName;
		
		log.info("----------- send messag {} to topic {}",message,targetTopic);
		
		Message<MessageDTO> sendMessage = MessageBuilder.withPayload(MessageDTO.builder().message(message).build())
		.setHeader(KafkaHeaders.TOPIC, targetTopic)
		.build();
		CompletableFuture<SendResult<String, Object>> messageinQueue = template.send(sendMessage);
//		CompletableFuture<SendResult<String, MessageDTO>> messageinQueue = template.send(topicName, 1, null, MessageDTO.builder().message(message).build());
		
		
		messageinQueue.whenComplete((mssg,ex) -> {
			if(ex==null) {
				
			log.info("send message {} on partion {} with offset {} ",((MessageDTO)mssg.getProducerRecord().value()).getMessage()
					,mssg.getRecordMetadata().partition(),mssg.getRecordMetadata().offset()
					);
			}
			
			
		});
		
	}
	public void sendStringMessage(String message , String key){
		
				CompletableFuture<SendResult<String, Object>> messageinQueue = template.send(topicName,key,MessageDTO.builder().message(message).build());
//				.setHeader("partitionKey", value)				
				
				messageinQueue.whenComplete((mssg,ex) -> {
					if(ex==null) {
						
					log.info("send message {} on partion {} with offset {} ",((MessageDTO)mssg.getProducerRecord().value()).getMessage()
							,mssg.getRecordMetadata().partition(),mssg.getRecordMetadata().offset()
							);
					}
					
					
				});
	
	}
	
	public void sendStringMessage(String message , int partion){
		
		log.info("----------- send messag {} to topic {}",message,topicName);
		
//		Message<MessageDTO> sendMessage = MessageBuilder.withPayload(MessageDTO.builder().message(message).build())
//				.setHeader(KafkaHeaders.TOPIC, topicName)
//				.build();
//		CompletableFuture<SendResult<String, MessageDTO>> messageinQueue = template.send(sendMessage);
		CompletableFuture<SendResult<String, Object>> messageinQueue = template.send(topicName, partion, null, MessageDTO.builder().message(message).build());
		
		
		messageinQueue.whenComplete((mssg,ex) -> {
			if(ex==null) {
				
				log.info("send message {} on partion {} with offset {} ",((MessageDTO)mssg.getProducerRecord().value()).getMessage()
						,mssg.getRecordMetadata().partition(),mssg.getRecordMetadata().offset()
						);
			}
			
			
		});
		
	}

}
