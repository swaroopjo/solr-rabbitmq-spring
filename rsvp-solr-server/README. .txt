Admin COnsole: localhost:8983/solr

The configs and Schema are loaded byt the data import needs to be manually triggerd.
 Go to admin console and trigger the import. 

Run the application on Jetty Server. Change the port of the Jetty to 8983 and context root to /solr

After the data is imported, The usrl to test : 
http://localhost:8983/solr/shopping-cart/select?q=country%3AGermany&wt=json&indent=true
 
This will bring all the Customers who are from Germany. 

If you want to create a new Project, We need to create a new directory inside solr cox that is where the application looks into for querying and loading. 
This directory should have conf and data. 
conf should contain schema.xml, solrConf.xml, dataaccess.xml and other html, text file for noice words. 
copy all the files because one is linked to another. 
you need to make changes only in the part of schema file that tells what field needs to be indexed. 
solrConfigFile add the dataimport handler or leave empty if you dont intend to. 
data access file should have your sql query. 


