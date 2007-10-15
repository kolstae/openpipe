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
}