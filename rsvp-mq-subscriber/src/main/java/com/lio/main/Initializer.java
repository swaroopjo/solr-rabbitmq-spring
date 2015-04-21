package com.lio.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Initializer {

	public static void main(String[] args){
		new ClassPathXmlApplicationContext("rabbit-listener-context.xml","rsvp-solr.xml");
	}
	
}
