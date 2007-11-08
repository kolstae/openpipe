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
package no.trank.openpipe.parse.xml;

import java.io.IOException;
import java.util.Collections;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

/**
 * @version $Revision$
 */
public class XMLParser implements Parser {
   private final XMLInputFactory factory;

   public XMLParser() {
      factory = XMLInputFactory.newInstance();
   }

   public XMLParser(XMLInputFactory factory) {
      this.factory = factory;
   }

   @Override
   public ParserResult parse(ParseData data) throws IOException, ParserException {
      try {
         String encoding = null;
         final XMLEventReader reader = factory.createXMLEventReader(data.getInputStream());
         final StringBuilder buf = new StringBuilder(Math.max(data.getLength() / 5, 64));
         try {
            while (reader.hasNext()) {
               final XMLEvent event = reader.nextEvent();
               if (event.isStartDocument()) {
                  final StartDocument start = (StartDocument) event;
                  encoding = start.getCharacterEncodingScheme();
               } else if (event.isCharacters()) {
                  final Characters chars = event.asCharacters();
                  if (!chars.isWhiteSpace()) {
                     buf.append(chars.getData().trim()).append(' ');
                  }
               }
            }
         } finally {
            try {
               reader.close();
            } catch (XMLStreamException e) {
               // Ignoring
            }
         }
         final ParserResultImpl result = new ParserResultImpl();
         if (buf.length() > 1) {
            buf.setLength(buf.length() - 1);
         }
         if (data.includeProperties()) {
            result.setProperties(Collections.singletonMap("encoding", encoding));
         }
         result.setText(buf.toString());
         return result;
      } catch (XMLStreamException e) {
         throw new ParserException(e);
      }
   }   
}
