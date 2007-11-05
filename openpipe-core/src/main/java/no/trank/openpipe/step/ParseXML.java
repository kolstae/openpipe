package no.trank.openpipe.step;

import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;

/**
 * 
 * @version $Revision$
 */
public class ParseXML extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(ParseXML.class);
   private static final Pattern WS_PATTERN = Pattern.compile("\\s+");
   private XMLInputFactory factory;
   private String fieldName;
   private Set<String> ignoredTags = Collections.emptySet();
   private Map<String, String> tagToFieldName = Collections.emptyMap();
   private boolean failOnXMLError = true;

   public ParseXML() {
      super("ParseXML");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final List<String> list = doc.getFieldValues(fieldName);
      for (String text : list) {
         try {
            final XMLEventReader reader = factory.createXMLEventReader(new StringReader(text));
            parseXML(doc, reader);
         } catch (XMLStreamException e) {
            if (failOnXMLError) {
               throw new PipelineException("Could not parse XML in field '" + fieldName + "'", e);
            } else {
               log.warn("Could not parse XML in field '" + fieldName + "'", e);
            }
         }
      }
      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   private void parseXML(Document doc, XMLEventReader reader) throws XMLStreamException {
      final Deque<String> stack = new ArrayDeque<String>();
      while (reader.hasNext()) {
         final XMLEvent event = reader.nextEvent();
         if (event.isStartElement()) {
            final StartElement elem = event.asStartElement();
            stack.push(elem.getName().getLocalPart());
            final Iterator it = elem.getAttributes();
            while (it.hasNext()) {
               final Attribute a = (Attribute) it.next();
               final String tag = a.getName().getLocalPart();
               final String data = a.getValue();
               if (isWanted(tag, data)) {
                  doc.addFieldValue(findToFieldName(tag), data);
               }
            }
         } else if (event.isEndElement()) {
            stack.pop();
         } else if (event.isCharacters()) {
            final String tag = stack.peek();
            final String data = event.asCharacters().getData();
            if (isWanted(tag, data)) {
               doc.addFieldValue(findToFieldName(tag), data);
            }
         }
      }
   }

   private boolean isWanted(String tag, String data) {
      return !ignoredTags.contains(tag) && !isBlank(data);
   }

   private static boolean isBlank(String data) {
      return WS_PATTERN.matcher(data).matches();
   }

   private String findToFieldName(String tag) {
      final String fieldName = tagToFieldName.get(tag);
      return fieldName == null ? tag : fieldName;
   }

   @Override
   public void prepare() throws PipelineException {
      if (factory == null) {
         factory = XMLInputFactory.newInstance();
      }
      if (factory.isPropertySupported(XMLInputFactory.IS_COALESCING)) {
         factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
      } else {
         log.warn("XMLInputFactory: {} does not support coalescing", factory.getClass().getName());
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public String getFieldName() {
      return fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   public Set<String> getIgnoredTags() {
      return ignoredTags;
   }

   public void setIgnoredTags(Set<String> ignoredTags) {
      this.ignoredTags = ignoredTags;
   }

   public Map<String, String> getTagToFieldName() {
      return tagToFieldName;
   }

   public void setTagToFieldName(Map<String, String> tagToFieldName) {
      this.tagToFieldName = tagToFieldName;
   }

   public boolean isFailOnXMLError() {
      return failOnXMLError;
   }

   public void setFailOnXMLError(boolean failOnXMLError) {
      this.failOnXMLError = failOnXMLError;
   }

   public XMLInputFactory getFactory() {
      return factory;
   }

   public void setFactory(XMLInputFactory factory) {
      this.factory = factory;
   }
}
