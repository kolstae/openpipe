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
import java.io.InputStream;

import junit.framework.TestCase;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.ParserResult;

/**
 * @version $Revision$
 */
public class XMLParserTest extends TestCase {

   public void testParse() throws Exception {
      final XMLParser parser = new XMLParser();
      final ParserResult result = parser.parse(new TestData());
      final String text = result.getText();
      assertNotNull(text);
      assertTrue(text.indexOf("ignored") < 0);
      assertTrue(text.indexOf("function matchwo(a,b)") >= 0);
      assertTrue(text.indexOf('>') >= 0);
      assertEquals("UTF-8", result.getProperties().get("encoding"));
   }

   private static class TestData implements ParseData {
      @Override
      public InputStream getInputStream() throws IOException {
         final InputStream in = getClass().getResourceAsStream("/xml/test.xml");
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
         return "dummy.xml";
      }
   }
}
