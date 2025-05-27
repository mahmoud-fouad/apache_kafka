package com.mfouad.kafkaprod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
//import org.springframework.boot.test.system.OutputCaptureExtension;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
//@ExtendWith(OutputCaptureExtension.class)
public class KafkaProducerApplicationTests {
	
	@Autowired
	Producer pr;
	Random random = new Random();
	
	@Autowired
	AvroProducer avroPro;

//	@Test
	public void sendToPartion0() {
		
		for(int i =0 ; i< 100 ; i++) {
		pr.sendStringMessage("welcom "+i,0);
		}
		
	}
	
//	@Test
	public void sendToPartion1() {
		
		for(int i =0 ; i< 100 ; i++) {
			pr.sendStringMessage("welcom "+i,1);
		}
	}
	
//	@Autowired
//	CapturedOutput output;
	
	@Test
	public void testSendStringMessage() {
		
		pr.sendStringMessage("welcom");
//		Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
		await().pollInterval(Duration.ofSeconds(3)).atMost(10,TimeUnit.SECONDS).untilAsserted(() -> {
			
//			output.getOut().contains("send message");
		});
		
	}
	
	@Test
	public void testSendMesagetoThirdFloor() {
		for(int i=0;i<20;i++)
		pr.sendStringMessage("welcom "+i,"thirdFloor");
//		Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
		await().pollInterval(Duration.ofSeconds(3)).atMost(10,TimeUnit.SECONDS).untilAsserted(() -> {
			
//			output.getOut().contains("send message");
		});
		
	}
	
//	@Test
	public void testAvroSendStringMessage() {
		
		avroPro.sendStringMessage("mahmoud");
		
		await().pollInterval(Duration.ofSeconds(3)).atMost(10,TimeUnit.SECONDS).untilAsserted(() -> {
			
//			output.getOut().contains("send message");
		});
		
	}
	
//	@Test
	public void testRetryAndDLT() {
		
		pr.sendStringMessage("exit");
	}

}
