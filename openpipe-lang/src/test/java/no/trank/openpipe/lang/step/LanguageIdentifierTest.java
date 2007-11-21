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
package no.trank.openpipe.lang.step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class LanguageIdentifierTest extends TestCase {

   public void testExecute() throws Exception {
      final LanguageIdentifier lang = new LanguageIdentifier();
      lang.setInputField("text");
      lang.prepare();
      testLanguage(lang, "en");
      testLanguage(lang, "no");
      testLanguage(lang, "sv");
   }

   private void testLanguage(LanguageIdentifier lang, String language) throws IOException, PipelineException {
      final Document doc = new Document();
      doc.setFieldValue(lang.getInputField(), getResourceAsString(language));
      assertEquals(PipelineStepStatus.DEFAULT, lang.execute(doc));
      assertEquals(language, doc.getFieldValue(lang.getLangField()));
   }

   private String getResourceAsString(final String lang) throws IOException {
      final String resName = "/documents/" + lang + ".txt";
      final InputStream in = getClass().getResourceAsStream(resName);
      assertNotNull("Could not find resource: " + resName, in);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      try {
         final char[] chars = new char[4096];
         final StringBuilder buf = new StringBuilder(4096);
         int read;
         while ((read = reader.read(chars)) >= 0) {
            buf.append(chars, 0, read);
         }
         return buf.toString();
      } finally {
         reader.close();
      }
   }
}
