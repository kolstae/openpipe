/*
 * Copyright 2007  T-Rank AS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.trank.openpipe.solr.producer;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds the update servlet and fires up jetty.
 * 
 * @version $Revision$
 */
public class SolrDocumentProducer implements Runnable {
   private static final Logger log = LoggerFactory.getLogger(SolrDocumentProducer.class);
   
   private Server server;
   private SolrUpdateServlet solrUpdateServlet;
   private String updatePath;
   
   /**
    * Initialization - adds the update servlet 
    */
   public void init() {
      ServletHandler servletHandler = new ServletHandler();
      
      ServletHolder updateHolder = new ServletHolder(solrUpdateServlet);
      updateHolder.setName("updateServlet");
      servletHandler.addServlet(updateHolder);
      
      ServletMapping updateMapping = new ServletMapping();
      updateMapping.setPathSpec(updatePath);
      updateMapping.setServletName("updateServlet");

      servletHandler.addServletMapping(updateMapping);
      
      server.addHandler(servletHandler);
   }
   
   /**
    * Fires up jetty.
    */
   @Override
   public void run() {
      log.info("Starting Solr producer");
      startJetty();
   }
   
   private void startJetty() {
      try {
         if(!server.isRunning()) {
            server.start();
            log.info("Successfully started jetty");
         }
         else {
            log.info("Jetty is already running.");
         }
      } catch (Exception e) {
         log.error("Error starting jetty", e);
      }
   }
   
   // spring setters
   public void setServer(Server server) {
      this.server = server;
   }

   public void setSolrUpdateServlet(SolrUpdateServlet solrUpdateServlet) {
      this.solrUpdateServlet = solrUpdateServlet;
   }

   public void setUpdatePath(String updatePath) {
      this.updatePath = updatePath;
   }
}