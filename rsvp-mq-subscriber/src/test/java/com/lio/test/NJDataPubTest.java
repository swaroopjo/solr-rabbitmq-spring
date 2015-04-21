package com.lio.test;

import java.util.Random;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

@Configuration("classpath:rsvp-solr.xml")
public class NJDataPubTest {
	
	private SolrTemplate template = (SolrTemplate) new ClassPathXmlApplicationContext("rsvp-solr.xml").getBean("NJSolrOperations");
	
	public void saveDocumnt(SolrInputDocument doc){
		
		template.saveDocument(doc);
		template.commit();
	}

	public static void main(String[] args){
		NJDataPubTest test = new NJDataPubTest(); 
		
		SolrInputDocument doc = new SolrInputDocument(); 
		doc.addField("rsvp_id", new Random().nextInt((99999 - 1)+ 1) + 1);
		doc.addField("venue_name", "xyz");
		doc.addField("group_name", "xyz");
		doc.addField("group_country", "USA");
		
		test.saveDocumnt(doc);
	}
}
