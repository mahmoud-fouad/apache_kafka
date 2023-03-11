package com.mfouad.kafkacon;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.mfouad.kafkaprod.MessageDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Consumer {
	
	@KafkaListener(topics={"${topic.name}"}, groupId="mygroup")
	public void consume(MessageDTO message){
		
		log.info("in {} --------------------",this.getClass());
		log.info("receive {}",message);
		
	}
	

}
