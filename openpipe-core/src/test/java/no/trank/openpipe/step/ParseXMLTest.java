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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class ParseXMLTest extends TestCase {

   public void testParse() throws Exception {
      final Document doc = new Document();
      ParseXML xml = new ParseXML();
      doc.setFieldValue("xml", "<project><test abc=\"attrib\">Text that should be included</test><ignored>Ignored text</ignored></project>");
      xml.setFieldName("xml");
      final Set<String> ignoredTags = Collections.singleton("ignored");
      xml.setIgnoredTags(ignoredTags);
      final HashMap<String, String> tagToFieldName = new HashMap<String, String>();
      tagToFieldName.put("test", "testField");
      xml.setTagToFieldName(tagToFieldName);
      xml.prepare();
      xml.execute(doc);
      doc.removeField("xml");
      for (String tag : ignoredTags) {
         assertTrue(doc.getFieldValues(tag).isEmpty());
      }
      for (Map.Entry<String, String> e : tagToFieldName.entrySet()) {
         assertTrue(doc.getFieldValues(e.getKey()).isEmpty());
         assertFalse(doc.getFieldValues(e.getValue()).isEmpty());
      }
   }
}
 