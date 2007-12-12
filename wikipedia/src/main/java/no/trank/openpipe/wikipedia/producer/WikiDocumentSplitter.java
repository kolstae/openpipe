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
package no.trank.openpipe.wikipedia.producer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Splits a wikipedia dump into <page>...</page> chunks.
 *
 * @version $Revision$
 */
public class WikiDocumentSplitter implements Iterator<String> {
   private static final Logger log = LoggerFactory.getLogger(WikiDocumentSplitter.class);
   private static final String PAGE_ELEMENT = "page";

   private final XMLEventReader xmlEventReader;
   private final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
   private String next;

   public WikiDocumentSplitter(InputStream input) throws XMLStreamException {
      if (input != null) {
         xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(input);
      } else {
         throw new IllegalArgumentException("Input can not be <null>");
      }
   }

   @Override
   public boolean hasNext() {
      if (next != null) {
         return true;
      } else {
         try {
            while (xmlEventReader.hasNext()) {
               XMLEvent xmlEvent = xmlEventReader.nextEvent();
               if(xmlEvent.isStartElement()) {
                  String localPart = xmlEvent.asStartElement().getName().getLocalPart();
                  if (PAGE_ELEMENT.equals(localPart)) {
                     next = parsePage(xmlEvent);
                     return next != null;
                  }
               }
            }
            return false;
         } catch (XMLStreamException e) {
            throw new RuntimeException("Could not parse xml", e);
         } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported", e);
         }
      }
   }

   private String parsePage(XMLEvent pageStartEvent) throws XMLStreamException, UnsupportedEncodingException {
      XMLEvent xmlEvent = null;
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(out);
      try {
         xmlEventWriter.add(pageStartEvent);
         while (xmlEventReader.hasNext()) {
            xmlEvent = xmlEventReader.nextEvent();
            xmlEventWriter.add(xmlEvent);
            if (xmlEvent.isEndElement()) {
               String localPart = xmlEvent.asEndElement().getName().getLocalPart();
               if (PAGE_ELEMENT.equals(localPart)) {
                  xmlEventWriter.close();
                  return out.toString("UTF-8");
               }
            }
         }
         return null;
      } catch (XMLStreamException e) {
         if (xmlEvent != null) {
            log.info("Failed on event {}", xmlEvent);
         }
         throw e;
      }
   }

   @Override
   public String next() {
      if (hasNext()) {
         try {
            return next;
         } finally {
            next = null;
         }
      } else {
         throw new NoSuchElementException();
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("Operation not supported");
   }

   public void close() {
      try {
         xmlEventReader.close();
      } catch (XMLStreamException e) {
         // Do nothing
      }
   }
}
