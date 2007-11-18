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
package no.trank.openpipe.tutorial.intranet;

import no.trank.openpipe.reader.FileDocumentReader;
import no.trank.openpipe.solr.SolrHttpDocumentPoster;
import no.trank.openpipe.solr.step.SolrDocumentProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The intranet tutorial starting point. Collects documents from a directory and posts them to a Solr index.
 * 
 * <p>Starts a Spring XML application context with definitions from <code>classpath:intranetApplicationContext.xml</code>
 * <p>Takes one or two command line arguments:
 * <ul>
 *    <li>the path to the documents
 *    <li>[optional] the URL to the Solr application (default: http://localhost:8983/solr)
 * </ul>
 * 
 * @version $Revision:670 $
 */
public class Main {
   private static final Logger log = LoggerFactory.getLogger(Main.class);
   
   public static void main(String[] args) {
      if(args.length < 1) {
         usage();
      }
      else {
         try {
            ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("intranetApplicationContext.xml");
            try {
               FileDocumentReader directory = (FileDocumentReader) appContext.getBean("fileDocumentReader", FileDocumentReader.class);
               directory.setDirectory(args[0]);
               
               if (args.length > 1) {
                  SolrHttpDocumentPoster solrDocumentPoster = (SolrHttpDocumentPoster) appContext.getBean("solrDocumentPoster", SolrHttpDocumentPoster.class);
                  SolrDocumentProcessor solrDocumentProcessor = (SolrDocumentProcessor) appContext.getBean("solrDocumentProcessor", SolrDocumentProcessor.class);
               
                  String solrUrl = args[1];
                  solrDocumentPoster.setPostUrl(solrUrl + "/update");
                  solrDocumentProcessor.setSolrSchemaUrl(solrUrl + "/admin/get-file.jsp?file=schema.xml");
               }
               
               Runnable pipelineApplication = (Runnable) appContext.getBean("pipelineApplicationBean", Runnable.class);
               pipelineApplication.run();
            } finally {
               appContext.close();
            }
         } catch (BeansException e) {
            log.error("Spring error", e);
         }
      }
   }
   
   private static void usage() {
      System.out.println("java -jar ?.jar <directory> [<solr url>]");
   }
}