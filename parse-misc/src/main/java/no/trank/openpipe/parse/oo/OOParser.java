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
package no.trank.openpipe.parse.oo;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

/**
 * Parser for OpenOffice documents.
 * 
 * @version $Revision$
 */
public class OOParser implements Parser, Closeable {

   @Override
   public ParserResult parse(ParseData data) throws IOException, ParserException {
      ZipInputStream inputStream = new ZipInputStream(data.getInputStream());

      String text = null;
      Map<String, String> properties = new HashMap<String, String>();

      ZipEntry ze = null;
      while ((ze = inputStream.getNextEntry()) != null) {
         if (ze.getName().equals("content.xml")) {
            try {
               text = parseContent(inputStream);
            } catch (Exception e) {
               e.printStackTrace();
            }
         } else if (ze.getName().equals("meta.xml")) {
            try {
               parseMeta(inputStream, properties);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

      final ParserResultImpl result = new ParserResultImpl();
      result.setText(text);
      result.setTitle(properties.get("title"));
      if (data.includeProperties()) {
         result.setProperties(properties);
      }
      return result;
   }

   @Override
   public void close() throws IOException {
   }

   private static String parseContent(ZipInputStream zis) throws Exception {
      StringBuilder sb = new StringBuilder();

      XMLStreamReader reader = null;

      try {
         // exhaust the stream, but don't close it
         reader = XMLInputFactory.newInstance().createXMLStreamReader(
               new FilterInputStream(zis) {
                  @Override
                  public void close() {
                  };
               });

         boolean lastp = true;
         String inTag = null;
         String link = null;

         while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement()) {
               if ("p".equals(reader.getLocalName())) {
                  inTag = "p";
               } else if (reader.getLocalName().equals("a")) {
                  link = reader.getAttributeValue("http://www.w3.org/1999/xlink", "href");
                  inTag = "a";
               } else if (reader.getLocalName().startsWith("tab")) {
                  inTag = "tab";
               } else if (reader.getLocalName().equals("span")) {
                  inTag = "span";
               } else {
                  inTag = null;
               }
            } else if (reader.isCharacters() && inTag != null) {
               String text = reader.getText();

               if ("p".equals(inTag) && text.length() > 0) {
                  if (!lastp) {
                     sb.append('\n');
                  }
                  sb.append(text).append('\n');
                  lastp = true;
               } else if ("tab".equals(inTag)) {
                  sb.append("\t");
                  lastp = false;
               } else if ("a".equals(inTag)) {
                  sb.append(text).append(' ').append(link);
                  lastp = false;
               } else if ("span".equals(inTag) && text.length() > 0) {
                  if (!lastp) {
                     sb.append(' ');
                  }
                  sb.append(text);
                  lastp = false;
               }
            }
         }
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (Exception e) {
            }
         }
      }

      return sb.toString();
   }

   private static void parseMeta(ZipInputStream zis, Map<String, String> properties) throws Exception {
      XMLStreamReader reader = null;

      try {
         // exhaust the stream, but don't close it
         reader = XMLInputFactory.newInstance().createXMLStreamReader(
               new FilterInputStream(zis) {
                  @Override
                  public void close() {
                  };
               });

         String inTag = null;

         while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement()) {
               inTag = reader.getLocalName();
            } else if (reader.isCharacters()) {
               String text = reader.getText();
               if (text.length() > 0) {
                  if ("title".equals(inTag)) {
                     properties.put("title", text);
                  } else if ("language".equals(inTag)) {
                     properties.put("language", text);
                  } else if ("creation-date".equals(inTag)) {
                     properties.put("creation-date", text);
                  } else if ("print-date".equals(inTag)) {
                     properties.put("print-date", text);
                  } else if ("generator".equals(inTag)) {
                     properties.put("generator", text);
                  } else if ("creator".equals(inTag)) {
                     properties.put("creator", text);
                  }
               }
            }
         }
      } finally {
         if (reader != null) {
            try {
               reader.close();
            } catch (Exception e) {
            }
         }
      }
   }
}