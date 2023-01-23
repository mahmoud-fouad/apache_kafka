package com.mfouad.kafkaprod;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.launchdarkly.eventsource.EventSource;
import com.mfouad.kafkaprod.events.WikiMediaEventHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WikiProducer {
	
private KafkaTemplate<String, String> template;
	
	@Value("${topic.name}")
	String topicName;
	
	  String url = "https://stream.wikimedia.org/v2/stream/recentchange";
	
	
	public WikiProducer(KafkaTemplate<String, String> template) {
		this.template=template;
	}
	
	/*
	 * using event source to read wiki media data
	 */
	public void readWikiData() throws InterruptedException{
		log.info("in --------------------{}",this.getClass());
		WikiMediaEventHandler mediahandler =new WikiMediaEventHandler(template, topicName);
		EventSource.Builder builder =new EventSource.Builder(mediahandler,URI.create(url));
		EventSource eventSource = builder.build() ;
		//start event source;
		
		eventSource.start();
		TimeUnit.MINUTES.sleep(5);
		
	}

}
