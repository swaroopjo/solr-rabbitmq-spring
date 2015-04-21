# solr-rabbitmq-spring
This Project is an integration of Rabbit MQ and Apache Solr with Spring-Data.

Pre-Requisites:

Intall RabbitMQ 3.4.4 : http://www.rabbitmq.com/download.html This will install reLang as a dependancy

Install Jetty Plugin for Eclipse

  - Configure Jetty to run on 8983 port and change context root from "/" to "/solr"

The Application comes as three modules 

1. rsvp-mq-publisher: 

      Mq-Publisher Listens to the Rsvp feed data continously and adds the data to the ArrayblockingQueue.
      This queue is sniffed continously and extract the meet-ups happening in NewYork and NewJersey and publishes the messages to rsvp.ny and rsvp.nj Queus on Rabbit Mq. 
      
2. rsvp-mq-subscriber:

      mq-subscriber contains two Listeners NJListenser and NYListener which will be listening to the appropriate queues.  
      
      SolrJ is injected into  these classes using Spring Ioc. The Messages received by these Listeners are  published to Solr running on the Jetty Server.
      
      This solr client sends the data to appropriate cores configured on the solr server. 
      
3. solr-server: 

      Solr server is a customized version of Apache Solr which includes project specific schema and solr configuration.
      
      The published messages are Indexed by the solr and stored in the data direcories. There are two Cores that are configured inside solr (NY-core and NJ-core). 
      
      All the appropriate indexes are stored in these cores and can be retrieved by http calls to the server. 
      
Run: 

  Import all the three projects into eclipse. 
  
  Run solr-server on Jetty Server. Make sure it is deployed correctly by navigating to the URL: http://localhost:8983/ , this   should show the solr dashboard. 
  
  Start Rabbit MQ server by clicking on the "Rabbit Mq Service Start" from start menu.
  
  Run the Main class "Initializer.java" from rsvp-mq-publisher "com/lio/main/Initializer"
  
  Run the Main class "Initializer.java" from rsvp-mq-subscriber "com/lio/main/Initializer"

Workflow:

  Publisher(rsvp-mq-publisher) publishes the NY and NJ meetup data to Rabbit MQ server, Rabbit forwards the data to the        Listeners. 
  
  Listeners(rsvp-mq-subscriber) inturn publishes the extracted Data(venue, group_name, country) to the Solr Cores.

Verify:

  The data will be continously pushed to these cores which we can verify by navigating to the url: 
  http://localhost:8983/NY-core/select?q=*:*&wt=json&indent=true
 
  or 
  
  Navigate to the Solr Dashboard 
  --> Select NJ Core or NJ Core from the drop down on the left side. 
  --> Click on execute Query, This will show the results of the seaarch hits. 
  --> You can also search by a criteria by giving the name and value pair in the "q" text box (venue_name=Coffee) 
  --> This will result in an url http://localhost:8983/NY-core/select?q=venue_name%3D+Coffee&wt=json&indent=true
      and should result in the search responses as Json. 
  
