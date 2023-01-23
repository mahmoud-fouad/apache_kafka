package com.mfouad.kafkaprod;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@NoArgsConstructor
@ToString
/**
 * 
 * @author mahmoud
 * I have to create the class in same MessagDto package in producer as d-serialization depends on full Qualified Class name
 */
public class MessageDTO {

	private String message;
	
}
