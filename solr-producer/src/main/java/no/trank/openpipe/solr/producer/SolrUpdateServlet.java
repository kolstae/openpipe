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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import no.trank.openpipe.api.Pipeline;
import no.trank.openpipe.solr.producer.xml.XmlStreamDocumentReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   private final XMLInputFactory inputFactory;
   private final XMLOutputFactory outputFactory;

   public SolrUpdateServlet() {
      inputFactory = XMLInputFactory.newInstance();
      outputFactory = XMLOutputFactory.newInstance();
   }

   /**
    * Handles update POSTs. Keeps the connection open until all documents have been fed into the pipeline.
    * Sets the response status code to 200 on success, 400 on failure to read XML. 
    */
   @Override
   protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      long time = System.currentTimeMillis();

      log.debug("Post uri: {}", req.getRequestURI());

      int status;

      final InputStream is = req.getInputStream();

      try {
         final XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
         try {
            final XmlStreamDocumentReader documents = new XmlStreamDocumentReader(reader);
            if (pipeline.run(documents)) {
               status = documents.isFailure() ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_OK;
            } else {
               status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
         } finally {
            try {
               reader.close();
            } catch (XMLStreamException e) {
               // Igonring
            }
         }
      } catch (XMLStreamException e) {
         log.warn("Error creating xml stream", e);
         status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      } finally {
         is.close();
      }

      resp.setStatus(status);
      if (status == HttpServletResponse.SC_OK) {
         writeSuccess(resp, System.currentTimeMillis() - time);
      }
      else {
         writeFailure(resp, status);
      }

      resp.flushBuffer();

      log.info("Took {} ms. Success: {}", System.currentTimeMillis() - time, status);
   }
   
   private void writeSuccess(HttpServletResponse resp, long time) throws IOException {
      resp.setCharacterEncoding("UTF-8");
      
      XMLStreamWriter writer = null;
      try {
         writer = outputFactory.createXMLStreamWriter(resp.getOutputStream(), "UTF-8");
         writer.writeStartDocument("UTF-8", "1.0");
         
         writer.writeStartElement("response");
         writer.writeStartElement("lst");
         writer.writeAttribute("name", "responseHeader");
         writer.writeStartElement("int");
         writer.writeAttribute("name", "status");
         writer.writeCharacters("0");
         writer.writeEndElement();
         writer.writeStartElement("int");
         writer.writeAttribute("name", "QTime");
         writer.writeCharacters("" + time);
         writer.writeEndElement();
         writer.writeEndElement();
         writer.writeEndElement();
         writer.writeEndDocument();
      } catch (XMLStreamException e) {
         log.warn("Error writing success XML", e);
      }
      finally {
         if (writer != null) {
            try {
               writer.close();
            } catch (XMLStreamException e) {
               // ignore
            }
         }
      }
   }
   
   private void writeFailure(HttpServletResponse resp, int status) throws IOException {
      resp.setCharacterEncoding("ISO-8859-1");
      
      PrintWriter pw = new PrintWriter(resp.getOutputStream());
      pw.write("<html>\n");
      pw.write("<head>\n");
      pw.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/>\n");
      pw.write("<title>Error " + status + " </title>\n");
      pw.write("</head>\n");
      pw.write("<body><h2>HTTP ERROR: " + status + "</h2></body>\n");
      pw.write("</html>");
      pw.flush();
   }

   public void setPipeline(Pipeline pipeline) {
      this.pipeline = pipeline;
   }
}