package no.trank.openpipe.solr.analysis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class Base64IOTest extends TestCase {
   
   public void testWriteHeader() throws Exception {
      final byte[] buf = new byte[5];
      final ByteArrayInputStream bin = new ByteArrayInputStream(buf);
      final MyByteArrayOutputStream bout = new MyByteArrayOutputStream(buf);
      Base64IO.writeHeader(bout, false);
      int ver = Base64IO.readHeader(bin);
      assertFalse(Base64IO.isCompressed(ver));
      bout.reset();
      bin.reset();
      Base64IO.writeHeader(bout, false);
      assertFalse(Base64IO.readHeaderIsCompressed(bin));
      bout.reset();
      bin.reset();

      Base64IO.writeHeader(bout, true);
      ver = Base64IO.readHeader(bin);
      assertTrue(Base64IO.isCompressed(ver));
      bout.reset();
      bin.reset();
      Base64IO.writeHeader(bout, true);
      assertTrue(Base64IO.readHeaderIsCompressed(bin));
      bout.reset();
      bin.reset();
   }

   private static class MyByteArrayOutputStream extends ByteArrayOutputStream {
      private MyByteArrayOutputStream(byte[] bytes) {
         buf = bytes;
      }
   }
}