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
package no.trank.openpipe.step.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision$
*/
public class XMLStreamDocWriter implements DocumentWriter {
   private static final Logger log = LoggerFactory.getLogger(XMLStreamDocWriter.class);
   private XMLOutputFactory factory;
   private OutputStream stream;
   private XMLStreamWriter writer;

   @Override
   public void reset(File file) throws FileNotFoundException, XMLStreamException {
      stream = new BufferedOutputStream(new FileOutputStream(file));
      writer = getFactory().createXMLStreamWriter(stream, "UTF-8");
   }

   private XMLOutputFactory getFactory() {
      if (factory == null) {
         factory = XMLOutputFactory.newInstance();
      }
      return factory;
   }

   @Override
   public void startDocument(String rootElementName) throws XMLStreamException {
      writer.writeStartDocument();
      writer.writeStartElement(rootElementName);
   }

   @Override
   public void endDocument() throws XMLStreamException {
      writer.writeEndDocument();
      writer.close();
   }

   @Override
   public void startElement(String elementName) throws XMLStreamException {
      writer.writeCharacters("\n");
      writer.writeStartElement(elementName);
   }

   @Override
   public void addAttribute(String name, List<String> values) throws XMLStreamException {
      if (!values.isEmpty()) {
         writer.writeAttribute(name, values.get(0));
         final int size = values.size();
         if (size > 1) {
            log.warn("More than one value for attribute '{}', discarding {} values", name, size - 1);
         }
      }
   }

   @Override
   public void addElement(String name, List<String> values) throws XMLStreamException {
      for (String value : values) {
         writer.writeCharacters("\n  ");
         writer.writeStartElement(name);
         writer.writeCharacters(value);
         writer.writeEndElement();
      }
   }

   @Override
   public void endElement() throws XMLStreamException {
      writer.writeCharacters("\n");
      writer.writeEndElement();
   }

   @Override
   public void close() throws IOException {
      stream.close();
   }
}
