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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserResult;

/**
 * @version $Revision$
 */
public abstract class AbstractMsParserTest extends TestCase {
   protected ParserResult result;
   
   protected abstract Class<? extends Parser> getParserClass();
   protected abstract String getFileName();
   
   @Override
   protected void setUp() throws Exception {
      final Parser parser = getParserClass().newInstance();
      result = parser.parse(new TestData(getFileName()));
   }
   
   private static class TestData implements ParseData {
      private final String fileName;
      
      TestData(String fileName) {
         this.fileName = fileName;
      }
      
      @Override
      public InputStream getInputStream() throws IOException {
         final InputStream in = getClass().getResourceAsStream("/" + fileName);
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