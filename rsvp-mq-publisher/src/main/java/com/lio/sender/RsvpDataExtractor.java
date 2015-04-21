package com.lio.sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * RsvpDataExtractor Listens to the RSVP Feed Data and publishes it onto the Rabbit MQ
 * */
public class RsvpDataExtractor {

	public RsvpDataExtractor(){
		
	}
	
	private static final Logger logger = LoggerFactory
			.getLogger(RsvpDataExtractor.class);

	
	
	static String STREAMING_API_URL="http://stream.meetup.com/2/rsvps";
	ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(500);
	
	Thread dataExtractor = new Thread(
			new Runnable(){

		@Override
		public void run() {
			
			DefaultHttpClient client = new DefaultHttpClient();
	        HttpGet get = new HttpGet(STREAMING_API_URL);
	        HttpResponse response;
	        try {
	            //Execute
	            response = client.execute(get);
	            StatusLine status = response.getStatusLine();
	            if(status.getStatusCode() == 200){
	                InputStream inputStream = response.getEntity().getContent();
	                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	                String in;
	                //Read line by line
	                while((in = reader.readLine())!=null){
	                    try{
	                    	logger.debug(in);
	                       Object json = new JSONParser().parse(in);
	                        queue.add(json);
	                		Thread.sleep(1000);
	                    }catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        } catch (IOException e) {
	        	logger.error("RSVP server could be down. The application will resume after 10 minutes");
	           e.printStackTrace();
	           
	            try {
	            	//Wait for 10 minutes if the server is down.
	            	logger.debug("Server might be down. App will resume after 10 minutes");
	                Thread.sleep(10000);
	            } catch (InterruptedException e1) {
	            }
	        }
			
		}}
			);
	
	@Autowired
	AbstractConnectionFactory connectionFactory;
	
	public void feedData(){
		
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setExchange("FEED-EXCHANGE");
		
		this.dataExtractor.start();
		
		while(true){
			Object obj = queue.poll();
			if(obj != null){
				
				 JSONObject json = null;
					try {
						json = (JSONObject)new JSONParser().parse(obj.toString());
					} catch (ParseException e1) {
						logger.error("Could not convert the response into Json. Ingonring this line");
					}
			 
				if(json != null){
					
					JSONObject group = (JSONObject) json.get("group");
					 String country =(String) group.get("group_country");
					if(country.equalsIgnoreCase("us")){
						String state = (String)group.get("group_state");
						if(state.equalsIgnoreCase("NY")){
							rabbitTemplate.convertAndSend("rsvp.ny.key", obj.toString());
							logger.info("Sending Data to NY Data Listener");
						}
						else if(state.equalsIgnoreCase("NJ")){
							rabbitTemplate.convertAndSend("rsvp.nj.key", obj.toString());
							logger.info("Sending Data to NJ Data Listener");
						}
					}
				}
				
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Thread Interrupted");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	
}
