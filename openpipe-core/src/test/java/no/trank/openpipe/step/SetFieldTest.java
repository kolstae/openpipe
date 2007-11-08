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

import java.util.Collections;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class SetFieldTest extends TestCase {
   private static final String FROM_FIELD = "from";
   private static final String ORIG_VALUE = "origValue";
   private static final String NEW_VALUE = "newValue";

   public void testExecute() throws Exception {
      final SetField step = new SetField();
      step.setFieldValueMap(Collections.singletonMap(FROM_FIELD, NEW_VALUE));
      step.setOverwrite(false);
      final Document doc = new Document();
      doc.setFieldValue(FROM_FIELD, ORIG_VALUE);
      step.execute(doc);
      assertEquals(ORIG_VALUE, doc.getFieldValue(FROM_FIELD));
      step.setOverwrite(true);
      step.execute(doc);
      assertEquals(NEW_VALUE, doc.getFieldValue(FROM_FIELD));
   }
}