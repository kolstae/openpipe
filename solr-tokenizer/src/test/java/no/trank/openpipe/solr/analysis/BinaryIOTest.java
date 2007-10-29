package no.trank.openpipe.solr.analysis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class BinaryIOTest extends TestCase {
   
   public void testWriteHeader() throws Exception {
      final byte[] buf = new byte[5];
      final ByteArrayInputStream bin = new ByteArrayInputStream(buf);
      final MyByteArrayOutputStream bout = new MyByteArrayOutputStream(buf);
      BinaryIO.writeHeader(bout, false);
      int ver = BinaryIO.readHeader(bin);
      assertFalse(BinaryIO.isCompressed(ver));
      bout.reset();
      bin.reset();
      BinaryIO.writeHeader(bout, false);
      assertFalse(BinaryIO.readHeaderIsCompressed(bin));
      bout.reset();
      bin.reset();

      BinaryIO.writeHeader(bout, true);
      ver = BinaryIO.readHeader(bin);
      assertTrue(BinaryIO.isCompressed(ver));
      bout.reset();
      bin.reset();
      BinaryIO.writeHeader(bout, true);
      assertTrue(BinaryIO.readHeaderIsCompressed(bin));
      bout.reset();
      bin.reset();
   }

   private static class MyByteArrayOutputStream extends ByteArrayOutputStream {
      private MyByteArrayOutputStream(byte[] bytes) {
         buf = bytes;
      }
   }
}