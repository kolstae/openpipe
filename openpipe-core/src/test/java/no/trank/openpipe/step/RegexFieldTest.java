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
import java.util.Map;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class RegexFieldTest extends TestCase {
   private RegexField regexField;
   
   @Override
   protected void setUp() throws Exception {
      regexField = new RegexField();
      
      Map<String, String> fieldNameMap = new HashMap<String, String>();
      fieldNameMap.put("in", "out");
      regexField.setFieldNameMap(fieldNameMap);
   }
   
   public void testExecute() throws Exception {
      Document doc = new Document();
      
      regexField.setFromPattern("^Norge\\/([^\\/]+)\\/.+$");
      regexField.setToPattern("$1");
      doc.setFieldValue("in", "Norge/Oslo/Tralala");

      regexField.execute(doc);
      assertEquals("Oslo", doc.getFieldValue("out"));
   }
   
   public void testCopyOnMiss() throws Exception {
      Document doc = new Document();
      
      regexField.setFromPattern("a");
      regexField.setToPattern(".");
      regexField.setCopyOnMiss(true);
      doc.setFieldValue("in", "b");
      regexField.execute(doc);
      assertEquals("b", doc.getFieldValue("out"));
 
      regexField.setCopyOnMiss(false);
      regexField.execute(doc);
      assertEquals(null, doc.getFieldValue("out"));
   }

   public void testMultiExecute() throws Exception {
      Document doc = new Document();
      
      regexField.setFromPattern("^Norge\\/([^\\/]+)\\/.+$");
      regexField.setToPattern("$1");
      doc.addFieldValue("in", "Norge/Oslo/Tralala");
      doc.addFieldValue("in", "Norge/Hedmark/Tralala");

      regexField.execute(doc);
      assertNotSame(null, doc.getFieldValues("out"));
      assertEquals(2, doc.getFieldValues("out").size());
      assertEquals("Oslo", doc.getFieldValues("out").get(0));
      assertEquals("Hedmark", doc.getFieldValues("out").get(1));
   }

   public void testMultiCopyOnMiss() throws Exception {
      Document doc = new Document();
      
      regexField.setFromPattern("^Norge\\/([^\\/]+)\\/.+$");
      regexField.setToPattern("$1");
      regexField.setCopyOnMiss(true);
      doc.addFieldValue("in", "Norge/Oslo/Tralala");
      doc.addFieldValue("in", "Sverige/Hedmark/Tralala");

      regexField.execute(doc);
      assertNotSame(null, doc.getFieldValues("out"));
      assertEquals(2, doc.getFieldValues("out").size());
      assertEquals("Oslo", doc.getFieldValues("out").get(0));
      assertEquals("Sverige/Hedmark/Tralala", doc.getFieldValues("out").get(1));
      
      //
      regexField.setCopyOnMiss(false);
      regexField.execute(doc);
      assertNotSame(null, doc.getFieldValues("out"));
      assertEquals(1, doc.getFieldValues("out").size());
      assertEquals("Oslo", doc.getFieldValues("out").get(0));
   }
   
   public void testPrepend() throws Exception {
      Document doc = new Document();
      
      regexField.setFromPattern("^(.*)$");
      regexField.setToPattern("file://$1");
      regexField.setCopyOnMiss(false);
      doc.addFieldValue("in", "/home/file.txt");

      regexField.execute(doc);
      assertEquals("file:///home/file.txt", doc.getFieldValue("out"));
   }

   public void testEncodePercentage() throws Exception {
      Document doc = new Document();
      
      regexField.setFromPattern("%");
      regexField.setToPattern("%25");
      regexField.setCopyOnMiss(true);
      doc.addFieldValue("in", "/home/file.txt");
      doc.addFieldValue("in", "/home/fi%le%.txt");

      regexField.execute(doc);
      assertNotSame(null, doc.getFieldValues("out"));
      assertEquals(2, doc.getFieldValues("out").size());
      assertEquals("/home/file.txt", doc.getFieldValues("out").get(0));
      assertEquals("/home/fi%25le%25.txt", doc.getFieldValues("out").get(1));
   }
}