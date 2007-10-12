package no.trank.openpipe.step;

import java.util.Collections;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision: 874 $
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