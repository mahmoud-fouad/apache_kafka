package com.mfouad.kafkaprod.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mfouad.kafkaprod.Producer;

//@RestController
//@RequestMapping("apis/v1")
public class API {
	
	Producer producer;
	
	public API(Producer pro) {
		producer=pro;
	}
	
	@GetMapping("publish")
	public ResponseEntity<Void> publish(@RequestParam("message") String message){
		
		producer.sendStringMessage(message);
		return ResponseEntity.ok().build();
		
	}
	

}
