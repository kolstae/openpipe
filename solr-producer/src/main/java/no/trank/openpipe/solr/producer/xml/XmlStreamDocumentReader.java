package no.trank.openpipe.solr.producer.xml;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.BaseAnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.PreResolvedAnnotation;

/**
 * @version $Revision$
*/
public class XmlStreamDocumentReader implements Iterable<Document> {
   private static final Logger log = LoggerFactory.getLogger(XmlStreamDocumentReader.class);
   private static final String TAG_DOC = "doc";
   private static final String TAG_FIELD = "field";
   private static final String TAG_FIELD_NAME = "name";
   private static final String TAG_BOOST = "boost";
   private final XMLStreamReader reader;
   private String operation; // operation is the first start tag (add, delete)
   private Document nextDocument;
   private boolean failure = false;

   public XmlStreamDocumentReader(XMLStreamReader reader) {
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
            try {
               while (!failure && nextDocument == null && reader.hasNext()) {
                  if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                     if (operation == null) {
                        operation = reader.getLocalName();
                     } else if (TAG_DOC.equals(reader.getLocalName())) {
                        nextDocument = readNextDocument();
                        nextDocument.setOperation(operation);
                     }
                  }
               }
            } catch (XMLStreamException e) {
               log.warn("Error reading posted Solr xml", e);
               failure = true;
               return false;
            }

            return nextDocument != null;
         }

         private Document readNextDocument() throws XMLStreamException {
            final Document doc = new Document();
            doc.addFieldValue(TAG_BOOST, reader.getAttributeValue(null, TAG_BOOST));
            while (reader.hasNext()) {
               final int type = reader.next();
               if (type == XMLStreamConstants.START_ELEMENT) {
                  if (TAG_FIELD.equals(reader.getLocalName())) {
                     final String fieldName = reader.getAttributeValue(null, TAG_FIELD_NAME);
                     final String boost = reader.getAttributeValue(null, TAG_BOOST);
                     final BaseAnnotatedField field = new BaseAnnotatedField(reader.getElementText());
                     if (boost != null) {
                        field.add(TAG_BOOST, Arrays.asList(new PreResolvedAnnotation(boost)));
                     }
                     doc.addField(fieldName, field);

                  }
               }  else if (type == XMLStreamConstants.END_ELEMENT) {
                  if (TAG_DOC.equals(reader.getLocalName())) {
                     return doc;
                  }
               }
                  
            }
            throw new XMLStreamException("Unclosed <doc/> element");
         }

         public Document next() {
            if (!hasNext()) {
               throw new NoSuchElementException();
            }
            try {
               return nextDocument;
            } finally {
               nextDocument = null;
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public boolean isFailure() {
      return failure;
   }
}
