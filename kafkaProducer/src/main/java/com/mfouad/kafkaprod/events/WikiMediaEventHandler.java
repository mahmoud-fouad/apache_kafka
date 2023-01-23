package com.mfouad.kafkaprod.events;
import org.springframework.kafka.core.KafkaTemplate;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class WikiMediaEventHandler implements EventHandler  {
	
	KafkaTemplate<String, String> kafkaTemple ; 
	
	String topicName;
	

	@Override
	public void onClosed() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onComment(String arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String message, MessageEvent event) throws Exception {
		log.info("{} --------------",this.getClass());
		log.info("revice message {}");
		log.debug ("revice message {}" , event.getData());
		
		
		kafkaTemple.send(topicName, event.getData());
		
	}

	@Override
	public void onOpen() throws Exception {
		log.debug("{} --------------",this.getClass());
		log.debug("open the event");
		
	}
}
