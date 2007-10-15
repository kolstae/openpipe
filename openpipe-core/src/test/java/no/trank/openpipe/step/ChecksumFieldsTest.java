package no.trank.openpipe.step;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.util.HexUtil;

/**
 * @version $Revision$
 */
public class ChecksumFieldsTest extends TestCase {
   private static final String IN_FIELD = "text";
   private static final String OUT_FIELD = "md5sum";
   private static final String TEXT = "This is the text";
   private static final byte[] BYTES;

   public void testExecute() throws Exception {
      final ChecksumFields step = new ChecksumFields();
      step.setFieldNames(Arrays.asList(IN_FIELD));
      step.setOutField(OUT_FIELD);
      testAlgo(step, null);
      testAlgo(step, "SHA1");
      try {
         testAlgo(step, "Dillbert_12142");
         fail("Should fail!");
      } catch (Exception e) {
         // Success
      }
   }

   private static void testAlgo(ChecksumFields step, String algorithm) throws Exception {
      final Document doc = new Document();
      doc.setFieldValue(IN_FIELD, TEXT);
      if (algorithm != null) {
         step.setAlgorithm(algorithm);
      }
      final MessageDigest digest = MessageDigest.getInstance(step.getAlgorithm());
      step.prepare();
      step.execute(doc);
      step.finish(true);
      final byte[] bytes = digest.digest(BYTES);
      final String value = doc.getFieldValue(OUT_FIELD);
      assertNotNull(value);
      final byte[] hash = HexUtil.toBytes(value);
      assertEquals(bytes.length, hash.length);
      assertTrue(Arrays.equals(bytes, hash));
   }

   static {
      try {
         BYTES = TEXT.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
      }
   }
}