package com.lio.listener;

import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

/**
 * Rsvp Listener listens to the messages on FEED-EXCHANGE that has a Routing pattern rsvp.*
 * 
 *  
 * */
public class RsvpNYListener implements MessageListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(RsvpNYListener.class);
	
	private SolrTemplate sTemplate = (SolrTemplate) new ClassPathXmlApplicationContext("rsvp-solr.xml").getBean("NYSolrOperations");

	public RsvpNYListener(){
		logger.info("Listner Initialized. Listening on rsvpNYQueue");
	}
	

		/**
		 * Called when the message is recieved.
		 * */
		public void onMessage(Message message) {
		String messageBody= new String(message.getBody());
		logger.debug("Listener received message----->"+messageBody);
		try{
			logger.debug("Seding Document to Solr NY-Core");
			publishDocument(messageBody);
		}
		catch(Exception e){
			e.printStackTrace();
		}
			
		}
		
			
		public void publishDocument(String msg){
			SolrInputDocument doc = new SolrInputDocument();
			try {
				JSONObject rsvp = (JSONObject)new JSONParser().parse(msg);
				doc.addField("rsvp_id", rsvp.get("rsvp_id"));
				doc.addField("venue_name", ((JSONObject)rsvp.get("venue")).get("venue_name"));
				doc.addField("group_name", ((JSONObject)rsvp.get("group")).get("group_name"));
				doc.addField("group_country", ((JSONObject)rsvp.get("group")).get("group_country"));
				sTemplate.saveDocument(doc);
				sTemplate.commit();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("unable to commit the document to the Solr Instance");
			}
			
		}
		
}

