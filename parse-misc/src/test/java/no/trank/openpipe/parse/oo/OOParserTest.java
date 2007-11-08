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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.ParserResult;

/**
 * @version $Revision$
 */
public class OOParserTest extends TestCase {

   public void testParseODP() throws Exception {
      final OOParser parser = new OOParser();
      final ParserResult result = parser.parse(new TestData("test.odp"));
      final String text = result.getText();
      
      assertNotNull(text);
      assertEquals(true, text.contains("Slide 1"));
      assertEquals(true, text.contains("Slide 2"));
      assertEquals(true, text.contains("Text on slide 2"));  
      
      assertEquals("Frode Johannesen", result.getProperties().get("creator"));
   }
   
   public void testParseODS() throws Exception {
      final OOParser parser = new OOParser();
      final ParserResult result = parser.parse(new TestData("test.ods"));
      final String text = result.getText();
      
      assertNotNull(text);
      assertEquals(true, text.contains("Cell text"));
      assertEquals(true, text.contains("4"));
      assertEquals(true, text.contains("Sheet 2 text"));  
      
      assertEquals("Frode Johannesen", result.getProperties().get("creator"));
   }
   
   public void testParseODT() throws Exception {
      final OOParser parser = new OOParser();
      final ParserResult result = parser.parse(new TestData("test.odt"));
      final String text = result.getText();
      
      assertNotNull(text);
      assertEquals(true, text.contains("Some text"));
      assertEquals(true, text.contains("Text"));
      assertEquals(true, text.contains("in"));  
      assertEquals(true, text.contains("a"));
      assertEquals(true, text.contains("table"));
      assertEquals(true, text.contains("Link http://link.link.link/"));
      
      assertEquals("Frode Johannesen", result.getProperties().get("creator"));
   }

   private static class TestData implements ParseData {
      private final String fileName;
      
      TestData(String fileName) {
         this.fileName = fileName;
      }
      
      @Override
      public InputStream getInputStream() throws IOException {
         final InputStream in = getClass().getResourceAsStream("/oo/" + fileName);
         assertNotNull(in);
         return in;
      }

      @Override
      public int getLength() {
         return 0;
      }

      @Override
      public boolean includeProperties() {
         return true;
      }

      @Override
      public String getFileName() {
         return fileName;
      }
   }
}