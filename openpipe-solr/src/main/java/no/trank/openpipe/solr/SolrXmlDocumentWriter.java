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
package no.trank.openpipe.solr;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @version $Revision$
 */
public class SolrXmlDocumentWriter {
   private static final String TAG_DOC = "doc";
   private static final String TAG_FIELD = "field";
   private final XMLEventWriter writer;
   private final XMLEventFactory factory;
   private final Writer out;
   private final boolean pretty;
   private final Characters newLine;
   private final StartElement idStart;
   private final EndElement idEnd;
   private final StartElement docStart;
   private final EndElement docEnd;
   private final StartElement addStart;
   private final EndElement addEnd;
   private final EndElement delEnd;
   private final EndElement fieldEnd;
   private final StartElement delStart;
   private final StartElement commStart;
   private final EndElement commEnd;
   private final StartElement optStart;
   private final EndElement optEnd;
   private final Characters newLineInd;

   public SolrXmlDocumentWriter(Writer out, UpdateOptions options) throws XMLStreamException {
      this(out, options, false);
   }
   
   public SolrXmlDocumentWriter(Writer out, UpdateOptions options, boolean pretty) throws XMLStreamException {
      this.out = out;
      this.pretty = pretty;
      factory = XMLEventFactory.newInstance();
      writer = XMLOutputFactory.newInstance().createXMLEventWriter(out);
      newLine = factory.createCharacters("\n");
      newLineInd = factory.createCharacters("\n  ");
      idStart = factory.createStartElement("", null, "id");
      idEnd = factory.createEndElement("", null, "id");
      docStart = factory.createStartElement("", null, TAG_DOC);
      docEnd = factory.createEndElement("", null, TAG_DOC);
      addStart = factory.createStartElement("", null, "add", options.createAddAttributes(factory), null);
      addEnd = factory.createEndElement("", null, "add");
      delStart = factory.createStartElement("", null, "delete", options.createDelAttributes(factory), null);
      delEnd = factory.createEndElement("", null, "delete");
      fieldEnd = factory.createEndElement("",null, TAG_FIELD);
      commStart = factory.createStartElement("", null, "commit", options.createCOAttributes(factory), null);
      commEnd = factory.createEndElement("", null, "commit");
      optStart = factory.createStartElement("", null, "optimize", options.createCOAttributes(factory), null);
      optEnd = factory.createEndElement("", null, "optimize");
   }

   public void startDoc(Map<String,String> attributes) throws XMLStreamException {
      if (pretty) {
         writer.add(newLine);
      }
      if (attributes.isEmpty()) {
         writer.add(docStart);
      } else {
         final List<Attribute> attribs = new ArrayList<Attribute>(attributes.size());
         for (Map.Entry<String,String> e : attributes.entrySet()) {
            attribs.add(factory.createAttribute(e.getKey(), e.getValue()));
         }
         writer.add(factory.createStartElement("", null, TAG_DOC, attribs.iterator(), null));
      }
   }

   public void endDoc() throws XMLStreamException {
      if (pretty) {
         writer.add(newLine);
      }
      writer.add(docEnd);
   }

   public void writeField(final String fieldName, String fieldContent) throws XMLStreamException {
      Iterator<?> attributes = Arrays.asList(factory.createAttribute("name", fieldName)).iterator();
      if (pretty) {
         writer.add(newLineInd);
      }
      writer.add(factory.createStartElement("", null, TAG_FIELD, attributes, null));
      writer.add(factory.createCharacters(fieldContent));
      writer.add(fieldEnd);
   }

   public void startAdd() throws XMLStreamException {
      if (pretty) {
         writer.add(newLine);
      }
      writer.add(addStart);
   }

   public void endAdd() throws XMLStreamException {
      if (pretty) {
         writer.add(newLine);
      }
      writer.add(addEnd);
   }

   private void delete(XMLEvent... deleteBodyElements) throws XMLStreamException {
      if (pretty) {
         writer.add(newLine);
      }
      writer.add(delStart);
      for (XMLEvent deleteBodyElement : deleteBodyElements) {
         writer.add(deleteBodyElement);
      }
      writer.add(delEnd);
   }

   public void deleteById(String id) throws XMLStreamException {
      delete(idStart, factory.createCharacters(id), idEnd);
   }

   public void deleteById(List<String> ids) throws XMLStreamException {
      final XMLEvent[] deleteEvents = new XMLEvent[ids.size() * 3];
      int eventIdx = 0;
      for (String id : ids) {
         deleteEvents[eventIdx++] = idStart;
         deleteEvents[eventIdx++] = factory.createCharacters(id);
         deleteEvents[eventIdx++] = idEnd;
      }
      delete(deleteEvents);
   }

   public void deleteByQuery(String query) throws XMLStreamException {
      delete(factory.createStartElement("", null, "query"), factory.createCharacters(query),
            factory.createEndElement("", null, "query"));
   }

   public void commit() throws XMLStreamException {
      if (pretty) {
         writer.add(newLine);
      }
      writer.add(commStart);
      writer.add(commEnd);
   }

   public void optimize() throws XMLStreamException {
      if (pretty) {
         writer.add(newLine);
      }
      writer.add(optStart);
      writer.add(optEnd);
   }


   public void close() throws XMLStreamException {
      writer.close();
      try {
         out.close();
      } catch (Exception e) {
         // Do nothing
      }
   }
}
