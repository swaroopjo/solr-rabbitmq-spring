package com.lio.main;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lio.sender.RsvpDataExtractor;

public class Initializer {
	
	
	public static void main(String[] args){
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("rabbit-sender-context.xml");
		
		ctx.getBean(RsvpDataExtractor.class).feedData();
		
		
	}

}
