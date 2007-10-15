package no.trank.openpipe.step;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class StripHtmlTest extends TestCase {
   private StripHtml stripHtml;
   
   @Override
   protected void setUp() throws Exception {
      stripHtml = new StripHtml();
      
      Map<String, String> fieldNameMap = new HashMap<String, String>();
      fieldNameMap.put("in", "out");
      stripHtml.setFieldNameMap(fieldNameMap);
   }
   
   public void testComments() throws Exception {
      Document doc = new Document();
      doc.setFieldValue("in", "<!-- html comment -->tralala<!--");
      
      stripHtml.execute(doc);
      
      assertEquals("tralala<!--", doc.getFieldValue("out"));
   }
   
   public void testTags() throws Exception {
      Document doc = new Document();

      doc.setFieldValue("in", "<div huff='jj>\\''>tralala");
      stripHtml.execute(doc);
      assertEquals("tralala", doc.getFieldValue("out"));
   }
   
   public void testEntities() throws Exception {
      Document doc = new Document();

      doc.setFieldValue("in", "trala&nbsp;la");
      stripHtml.execute(doc);
      assertEquals("trala la", doc.getFieldValue("out"));
   }
}