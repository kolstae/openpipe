package no.trank.openpipe.producer.solr;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.Pipeline;
import no.trank.openpipe.producer.solr.xml.XmlStreamDocumentReader;

/**
 * Translates Solr update posts into <code>Document</code> instances and feeds them into the <code>Pipeline</code> 
 * instance.
 *
 * @version $Revision$
 */
public class SolrUpdateServlet extends HttpServlet {
   private static final long serialVersionUID = 2086572939284017929L;
   private static final Logger log = LoggerFactory.getLogger(SolrUpdateServlet.class);

   private Pipeline pipeline;
   private final XMLInputFactory factory;

   public SolrUpdateServlet() {
      factory = XMLInputFactory.newInstance();
   }

   /**
    * Handles update POSTs. Keeps the connection open until all documents have been fed into the pipeline.
    * Sets the response status code to 200 on success, 500 on failure. 
    */
   @Override
   protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      long time = System.currentTimeMillis();

      log.debug("Post uri: {}", req.getRequestURI());

      boolean success = false;

      final InputStream is = req.getInputStream();

      try {
         final XMLStreamReader reader = factory.createXMLStreamReader(is);
         try {
            final XmlStreamDocumentReader documents = new XmlStreamDocumentReader(reader);
            success = pipeline.run(documents) && !documents.isFailure();
         } finally {
            try {
               reader.close();
            } catch (XMLStreamException e) {
               // Igonring
            }
         }
      } catch (XMLStreamException e) {
         log.warn("Error creating xml stream", e);
         success = false;
      } finally {
         is.close();
      }

      // TODO: content?
      resp.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.setContentLength(0);
      resp.getOutputStream().close();

      log.info("Took {} ms. Success: {}", System.currentTimeMillis() - time, success);
   }

   public void setPipeline(Pipeline pipeline) {
      this.pipeline = pipeline;
   }
}