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
package no.trank.openpipe.lemmatizer.parser;

import junit.framework.TestCase;
import no.trank.openpipe.lemmatizer.model.LemmaSuffix;
import no.trank.openpipe.lemmatizer.model.LemmatizeModel;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version $Revision$
 */
public class TextParserTest extends TestCase {
   private List<LemmaSuffix> lemmas;

   public void testParse() throws Exception {
      final String prefix = "ala";
      final String str = buildSuffixStr(prefix);
      final LemmatizeModel model = new LemmatizeModel();
      new TextParser().parse(new StringReader(str), model);
      final Iterator<String> it = model.get(prefix);
      for (LemmaSuffix lemma : lemmas) {
         if (!it.hasNext()) {
            fail("Suffix " + lemma + " missing from model");
         }
         assertEquals(prefix.substring(0, prefix.length() - lemma.getCut()) + lemma.getSuffix(), it.next());
      }
      if (it.hasNext()) {
         fail("Extra suffix " + it.next() + " in model");
      }
   }

   public void testParseSuffixes() throws Exception {
      List<LemmaSuffix> suffixes = TextParser.parseSuffixes("fbis\t1", 5);
      assertEquals(1, suffixes.size());
      final LemmaSuffix suffix = suffixes.get(0);
      assertEquals(1, suffix.getCut());
      assertEquals("", suffix.getSuffix());
      final String prefix = "ala";
      final String str = buildSuffixStr(prefix);
      suffixes = TextParser.parseSuffixes(str, prefix.length() + 1);
      assertSuffixes(suffixes);
      suffixes = TextParser.parseSuffixes(str + "\t", prefix.length() + 1);
      assertSuffixes(suffixes);
   }

   private void assertSuffixes(List<LemmaSuffix> suffixes) {
      final Iterator<LemmaSuffix> it = suffixes.iterator();
      for (LemmaSuffix lemma : lemmas) {
         if (!it.hasNext()) {
            fail("Suffix " + lemma + " missing from parsed suffices");
         }
         assertEquals(lemma,  it.next());
      }
   }

   private String buildSuffixStr(String prefix) {
      final StringBuilder sb = new StringBuilder();
      sb.append(prefix);
      for (LemmaSuffix suffix : lemmas) {
         sb.append('\t').append(suffix.getCut()).append(suffix.getSuffix());
      }
      return sb.toString();
   }

   @Override
   protected void setUp() throws Exception {
      lemmas = new ArrayList<LemmaSuffix>();
      lemmas.add(new LemmaSuffix(0, "nde"));
      lemmas.add(new LemmaSuffix(0, "ne"));
      lemmas.add(new LemmaSuffix(0, "nes"));
      lemmas.add(new LemmaSuffix(0, "r"));
      lemmas.add(new LemmaSuffix(0, "rs"));
      lemmas.add(new LemmaSuffix(0, "s"));
      lemmas.add(new LemmaSuffix(0, "st"));
      lemmas.add(new LemmaSuffix(1, ""));
      lemmas.add(new LemmaSuffix(1, "e"));
      lemmas.add(new LemmaSuffix(1, "en"));
      lemmas.add(new LemmaSuffix(1, "ens"));
      lemmas.add(new LemmaSuffix(1, "er"));
      lemmas.add(new LemmaSuffix(1, "et"));
      lemmas.add(new LemmaSuffix(1, "ets"));
      lemmas.add(new LemmaSuffix(1, "i"));
      lemmas.add(new LemmaSuffix(1, "is"));
      lemmas.add(new LemmaSuffix(1, "ne"));
      lemmas.add(new LemmaSuffix(1, "s"));
      lemmas.add(new LemmaSuffix(1, "t"));
      lemmas.add(new LemmaSuffix(1, "te"));
      lemmas.add(new LemmaSuffix(3, "el"));
      lemmas.add(new LemmaSuffix(3, "eler"));
      lemmas.add(new LemmaSuffix(3, "ol"));
   }
}