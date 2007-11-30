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
package no.trank.openpipe.step;

import java.util.HashMap;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class ParseXMLXPathTest extends TestCase {
   private ParseXMLXPath xml;

   public void testParse() throws Exception {
      final Document doc = new Document();
      doc.setFieldValue("xml", getTestXml());
      xml.setFieldName("xml");
      final HashMap<String, String> xpaths = new HashMap<String, String>();
      xpaths.put("page/title", "title");
      xpaths.put("page/id", "id");
      xpaths.put("page/revision/text", "text");

      xml.setXPathToFieldName(xpaths);
      xml.prepare();
      xml.execute(doc);

      assertEquals("Arbeiderpartiet", doc.getFieldValue("title"));
      assertEquals("1", doc.getFieldValue("id"));
      assertEquals("#REDIRECT [[Det norske Arbeiderparti]]", doc.getFieldValue("text"));
   }

   private String getTestXml() {
      return "  <page>\n" +
            "    <title>Arbeiderpartiet</title>\n" +
            "    <id>1</id>\n" +
            "    <revision>\n" +
            "      <id>201289</id>\n" +
            "      <timestamp>2004-01-07T11:00:14Z</timestamp>\n" +
            "      <contributor>\n" +
            "        <username>Samuelsen</username>\n" +
            "        <id>6</id>\n" +
            "      </contributor>\n" +
            "      <comment>#REDIRECT [[Det norske Arbeiderparti]]</comment>\n" +
            "      <text xml:space=\"preserve\">#REDIRECT [[Det norske Arbeiderparti]]</text>\n" +
            "    </revision>\n" +
            "  </page>";
   }

   @Override
   protected void setUp() throws PipelineException {
      xml = new ParseXMLXPath();      
   }
}