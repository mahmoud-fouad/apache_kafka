package com.mfouad.kafkaprod;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Producer {
	
	private KafkaTemplate<String, MessageDTO> template;
	
	@Value("${topic.name}")
	String topicName;
	
	public Producer(KafkaTemplate<String, MessageDTO> template) {
		this.template=template;
	}
	
	public void sendStringMessage(String message){
		
		log.info("----------- send messag {} to topic {}",message,topicName);
		
		Message<MessageDTO> sendMessage = MessageBuilder.withPayload(MessageDTO.builder().message(message).build())
		.setHeader(KafkaHeaders.TOPIC, topicName)
		.build();
		template.send(sendMessage);
	}

}
