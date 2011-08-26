package no.trank.openpipe.reader;

import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentProducer;
import no.trank.openpipe.api.document.DomRawData;
import no.trank.openpipe.config.annotation.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.dom.DOMResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Streams an arbitrarily large XML file while pulling out each sub-tree rooted at a matching element name. The
 * DOM is put into a {@link DomRawData}.
 *
 * @author David Smiley - dsmiley@mitre.org
 */
public class MultiXmlDocumentReader implements DocumentProducer
{

   private final Logger log = LoggerFactory.getLogger(getClass());

   @NotNull
   private Resource input;
   private InputStream inputStream;//fetched lazily from input once needed; closed in close().

   private XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
   private XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
   private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

   @NotNull
   private QName elemMatch;

   @Override
   public void init()  {

   }

   @Override
   public void close() {
      if (inputStream != null) {
         try {
            inputStream.close();
         } catch (IOException e) {
            log.warn(e.toString(),e);
         }
      }
   }

   @Override
   public void fail() {
      close();
   }

   @Override
   public Iterator<Document> iterator() {
      if (inputStream != null) {
         throw new IllegalStateException("already fetched inputStream!");
      }
      return new MultiXmlDocumentIterator();
   }

   public void setInput(Resource input) {
      this.input = input;
   }

   public void setElemMatch(QName elemMatch) {
      this.elemMatch = elemMatch;
   }

   class MultiXmlDocumentIterator extends AbstractIterator<Document> {

      private XMLEventReader eventReader;
      private StartElement currStartEle;

      @Override
      protected Document computeNext() {
         try {
            if (currStartEle == null)
               currStartEle = readTillMatchingStartEle();
            if (currStartEle == null) {
               return endOfData();
            } else {
               Document doc = readAndBuildDocument();
               currStartEle = null;
               return doc;
            }
         } catch (Exception e) {
            throw Throwables.propagate(e);
         }
      }

      private StartElement readTillMatchingStartEle() throws Exception {
         assert currStartEle == null;
         if (eventReader == null) {
            inputStream = input.getInputStream();
            eventReader = xmlInputFactory.createXMLEventReader(inputStream);
         }
         while(eventReader.hasNext()) {
            XMLEvent evt = eventReader.nextEvent();
            if (evt.isStartElement()) {
               StartElement ele = evt.asStartElement();
               if (ele.getName().equals(elemMatch)) {
                  return ele;
               }
            }
         }
         return null;
      }

      private Document readAndBuildDocument() throws Exception {
         assert currStartEle != null;
         DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
         DOMResult domResult = new DOMResult(docBuilder.newDocument());
         XMLEventWriter eventWriter = xmlOutputFactory.createXMLEventWriter(domResult);
         int depth = 1;
         eventWriter.add(currStartEle);
         while(eventReader.hasNext() && depth > 0) {
            XMLEvent evt = eventReader.nextEvent();
            eventWriter.add(evt);
            if (evt.isStartElement()) {
               depth++;
            } else if (evt.isEndElement()) {
               depth--;
            }
         }
         return new Document(new DomRawData(null,domResult.getNode()));
      }
   }

}
