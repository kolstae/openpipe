package no.trank.openpipe.producer.solr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import no.trank.openpipe.api.Pipeline;
import no.trank.openpipe.api.document.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates Solr update posts into <code>Document</code> instances and feeds them into the <code>Pipeline</code> instance.
 * 
 * @version $Revision$
 */
public class SolrUpdateServlet extends HttpServlet {
   private static final long serialVersionUID = 2086572939284017929L;
   private static final Logger log = LoggerFactory.getLogger(SolrUpdateServlet.class);
   
   private Pipeline pipeline;
   
   /**
    * Handles update POSTs. Keeps the connection open until all documents have been fed into the pipeline.
    * Sets the response status code to 200 on success, 404 on failure. 
    */
   @Override
   protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      long time = System.currentTimeMillis();
      
      log.debug("Post uri: " + req.getRequestURI());

      boolean success = false;
      
      final InputStream is = req.getInputStream();
      XMLStreamReader reader = null;
         
      try {
         reader = XMLInputFactory.newInstance().createXMLStreamReader(is);
         Iterable<Document> iterable = new XmlStreamDocumentIterable(reader);
         success = pipeline.run(iterable);
      } catch (XMLStreamException e) {
         log.warn("Error creating xml stream", e);
         success = false;
      } catch (FactoryConfigurationError e) {
         log.warn("Error creating factory for xml stream", e);
         success = false;
      }
      finally {
         if(reader != null) {
            try {
               reader.close();
            }
            catch(Exception e) {
            }
         }
         if(is != null) {
            is.close();
         }
      }
      
      // TODO: content?
      if(success) {
         resp.setStatus(200);
         resp.setContentLength(0);
      }
      else {
         resp.setStatus(404);
         resp.setContentLength(0);
      }
      
      ServletOutputStream out = resp.getOutputStream();
      out.flush();
      out.close();
      
      log.info("Took {} ms. Success: {}", System.currentTimeMillis() - time, success);
   }
   
   static class XmlStreamDocumentIterable implements Iterable<Document> {
      private final XMLStreamReader reader;
      private String operation; // operation is the first start tag (add, delete)
      private Document nextDocument;
      
      XmlStreamDocumentIterable(XMLStreamReader reader) {
         this.reader = reader;
      }

      /**
       * Reads from the xml stream, ie the request input stream, and constructs
       * <code>Document</code> instances on demand rather than keeping an internal
       * list, which could potentially be very large.   
       * The operation is always set to the first start tag in the xml document.
       * 
       * @return a document Iterator
       */
      public Iterator<Document> iterator() {
         return new Iterator<Document>() {
            public boolean hasNext() {
               if(nextDocument == null) {
                  boolean done = false;
                  try {
                     while(!done && reader.hasNext()) {
                        int type = reader.next();
                        if(type == 1) {  // START
                           if(operation == null) {
                              operation = reader.getLocalName();
                           }
                           else if("doc".equals(reader.getLocalName())) {
                              nextDocument = new Document();
                              nextDocument.setOperation(operation);
                           }
                           else if("field".equals(reader.getLocalName())) {
                              String key = reader.getAttributeValue(0);
                              String value = reader.getElementText();
                              nextDocument.addFieldValue(key, value);
                           }
                        }
                        else if(type == 2) {  // END
                           if("doc".equals(reader.getLocalName())) {
                              done = true;
                           }
                        }
                     }
                  } catch (XMLStreamException e) {
                     log.warn("Error reading posted Solr xml", e);
                     return false;
                  }
               }
               
               return nextDocument != null;
            }
            
            public Document next() {
               hasNext();
               Document ret = nextDocument;
               nextDocument = null;
               return ret;
            }
            
            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }
   }

   // spring setters
   public void setPipeline(Pipeline pipeline) {
      this.pipeline = pipeline;
   }
}