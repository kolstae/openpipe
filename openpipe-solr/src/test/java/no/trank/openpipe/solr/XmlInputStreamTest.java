package no.trank.openpipe.solr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class XmlInputStreamTest extends TestCase {
   public void testRead() throws Exception {
      String testString = "<xml tralala>";
      InputStream is = new XmlInputStream(new ByteArrayInputStream(("   " + testString).getBytes()));
      
      for(int i = 0; i < testString.length(); ++i) {
         assertEquals(is.read(), testString.charAt(i));
      }
      
      assertEquals(is.read(), -1);
   }

   public void testReadBuffer() throws Exception {
      String testString = "<xml tralala>";
      InputStream is = new XmlInputStream(new ByteArrayInputStream(("   " + testString).getBytes()));
      
      byte[] b = new byte[4096];
      assertEquals(testString.length(), is.read(b, 0, b.length));
      
      for(int i = 0; i < testString.length(); ++i) {
         assertEquals(testString.charAt(i), b[i]);
      }
   }
}
