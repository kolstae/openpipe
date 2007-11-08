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
package no.trank.openpipe.parse.ms;

import no.trank.openpipe.parse.api.Parser;


/**
 * @version $Revision$
 */
public class WordParserTest extends AbstractMsParserTest {
   public void testParsePPT() throws Exception {
      final String text = result.getText();
      
      assertNotNull(text);
      assertEquals(true, text.contains("Some text Text in a table  HYPERLINK \"http://link.link.link/\" Link"));
      assertEquals("Frode Johannesen", result.getProperties().get("author"));
      assertEquals("3", result.getProperties().get("revNumber"));
   }
   
   @Override
   protected Class<? extends Parser> getParserClass() {
      return WordParser.class;
   }
   
   @Override
   protected String getFileName() {
      return "test.doc";
   }
}