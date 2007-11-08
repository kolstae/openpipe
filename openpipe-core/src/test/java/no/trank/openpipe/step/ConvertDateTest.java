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
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class ConvertDateTest extends TestCase {
   public void testExecute() throws Exception {
      ConvertDate convertDate = new ConvertDate();
      
      Map<String, String> fieldNameMap = new HashMap<String, String>();
      fieldNameMap.put("in", "out");
      convertDate.setFieldNameMap(fieldNameMap);
      
      LinkedHashMap<String, String> patternMap = new LinkedHashMap<String, String>();
      patternMap.put("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'");
      convertDate.setPatternMap(patternMap);
      convertDate.prepare();
      
      Document doc = new Document();
      doc.setFieldValue("in", "2009-02-03 12:13:14");
      
      convertDate.execute(doc);
      
      assertEquals("2009-02-03T12:13:14Z", doc.getFieldValue("out"));
   }
}