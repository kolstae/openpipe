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

import java.util.List;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class HierarchicalSplitterTest extends TestCase {
   private HierarchicalSplitter hierarchicalSplitter;
   
   @Override
   protected void setUp() throws Exception {
      hierarchicalSplitter = new HierarchicalSplitter();
      hierarchicalSplitter.setFromFieldName("in");
      hierarchicalSplitter.setToFieldName("out");
      hierarchicalSplitter.setLevelSplit("/");
      hierarchicalSplitter.setAlternativeSplit("¦");
   }
   
   public void testExecute() throws Exception {
      Document doc = new Document();
      doc.setFieldValue("in", "a/b");
      hierarchicalSplitter.execute(doc);
      
      List<String> values = doc.getFieldValues("out");
      assertEquals(2, values.size());
      assertTrue(values.contains("a"));
      assertTrue(values.contains("a/b"));
   }

   public void testAlternativeSplit() throws Exception {
      Document doc = new Document();
      doc.setFieldValue("in", "a/b¦c");
      hierarchicalSplitter.execute(doc);
      
      List<String> values = doc.getFieldValues("out");
      assertEquals(3, values.size());
      assertTrue(values.contains("a"));
      assertTrue(values.contains("a/b"));
      assertTrue(values.contains("a/c"));
   }

   public void testNumLevels() throws Exception {
      hierarchicalSplitter.setNumLevels(1);
      
      Document doc = new Document();
      doc.setFieldValue("in", "a/b¦c");
      hierarchicalSplitter.execute(doc);
      
      List<String> values = doc.getFieldValues("out");
      assertEquals(1, values.size());
      assertTrue(values.contains("a"));
   }
}