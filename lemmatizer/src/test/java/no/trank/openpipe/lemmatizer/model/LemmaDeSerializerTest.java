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
package no.trank.openpipe.lemmatizer.model;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @version $Revision$
 */
public class LemmaDeSerializerTest extends TestCase {

   public void testWriteRead() throws Exception {
      final List<LemmaSuffix> suffixes = new ArrayList<LemmaSuffix>();
      //abortlovs       1       1a      1ane    1anes   1ar     1ars    1as     1en     1ene    1enes   1ens    1er     1ers    1i      1is
      suffixes.add(new LemmaSuffix(1, ""));
      suffixes.add(new LemmaSuffix(1, "a"));
      suffixes.add(new LemmaSuffix(1, "ane"));
      suffixes.add(new LemmaSuffix(1, "anes"));
      suffixes.add(new LemmaSuffix(1, "ar"));
      suffixes.add(new LemmaSuffix(1, "ars"));
      suffixes.add(new LemmaSuffix(1, "as"));
      suffixes.add(new LemmaSuffix(1, "en"));
      suffixes.add(new LemmaSuffix(1, "ene"));
      suffixes.add(new LemmaSuffix(1, "enes"));
      suffixes.add(new LemmaSuffix(1, "ens"));
      suffixes.add(new LemmaSuffix(1, "er"));
      suffixes.add(new LemmaSuffix(1, "ers"));
      suffixes.add(new LemmaSuffix(1, "i"));
      suffixes.add(new LemmaSuffix(1, "is"));
      final byte[] data = LemmaDeSerializer.createLemmasData(suffixes);
      final String prefix = "abortlovs";
      final Iterator<String> it = LemmaDeSerializer.createIterator(prefix, data);
      for (LemmaSuffix suffix : suffixes) {
         if (!it.hasNext()) {
            fail("Suffix missing from data: " + suffix);
         }
         assertEquals(prefix.substring(0, prefix.length() - suffix.getCut()) + suffix.getSuffix(), it.next());
      }
      if (it.hasNext()) {
         fail("Extra suffix missing in data: " + it.next());
      }
   }
}