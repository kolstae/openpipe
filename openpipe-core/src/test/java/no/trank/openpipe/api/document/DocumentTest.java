package no.trank.openpipe.api.document;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class DocumentTest extends TestCase {

   public void testCRUDField() throws Exception {
      final Document doc = new Document();
      doc.setFieldValue("field", null);
      assertFalse(doc.containsField("field"));
      assertFalse(doc.removeField("field"));
      doc.setFieldValue("field", "value");
      assertTrue(doc.removeField("field"));
      assertFalse(doc.removeField("field"));
   }
}