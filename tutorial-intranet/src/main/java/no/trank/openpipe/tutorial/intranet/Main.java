package no.trank.openpipe.tutorial.intranet;

import no.trank.openpipe.reader.FileDocumentReader;
import no.trank.openpipe.solr.SolrHttpDocumentPoster;
import no.trank.openpipe.solr.step.SolrDocumentProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
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
