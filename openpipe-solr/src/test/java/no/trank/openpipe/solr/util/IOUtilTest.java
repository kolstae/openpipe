package no.trank.openpipe.solr.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision$
 */
public class IOUtilTest extends TestCase {
   private static final Logger log = LoggerFactory.getLogger(IOUtilTest.class);
   private Random rnd = new Random();
   
   public void testWriteReadUTF() throws Exception {
      final byte[] buf = new byte[1024];
      final ByteArrayInputStream bin = new ByteArrayInputStream(buf);
      final MyByteArrayOutputStream bout = new MyByteArrayOutputStream(buf);
      bin.mark(16);
      for (int i = 0; i < 200; i += 7) {
         final String text = generateRandomText(50, 150);
         IOUtil.writeUTF(bout, text);
         assertEquals(text, IOUtil.readUTF(bin));
         bout.reset();
         bin.reset();
      }
   }

   public void testLongWriteReadUTF() throws Exception {
      final ByteArrayOutputStream bout = new ByteArrayOutputStream(Short.MAX_VALUE + 4096);
      final String text = generateRandomText(Short.MAX_VALUE, 127);
      IOUtil.writeUTF(bout, text);
      final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
      assertEquals(text, IOUtil.readUTF(bin));
   }

   private String generateRandomText(int baseLen, int varLen) {
      final int length = rnd.nextInt(varLen) + baseLen;
      final char[] chars = new char[length];
      for (int i = 0; i < length; i++) {
         if (i % 13 == 7) {
            char c = getRandomChar(Character.MAX_VALUE, 128);
            chars[i] = c;
         } else {
            chars[i] = getRandomChar(127, 0);
         }
      }
      return new String(chars);
   }

   private char getRandomChar(int maxValue, int minValue) {
      char c = (char)(rnd.nextInt(maxValue - minValue) + minValue);
      while (!Character.isLetterOrDigit(c)) {
         c = (char)(rnd.nextInt(maxValue- minValue) + minValue);
      }
      return c;
   }

   public void testWriteReadNibble() throws Exception {
      final byte[] buf = new byte[16];
      final ByteArrayInputStream bin = new ByteArrayInputStream(buf);
      final MyByteArrayOutputStream bout = new MyByteArrayOutputStream(buf);
      bin.mark(16);
      for (int i = 0; i < Integer.MAX_VALUE; i += 7) { 
         IOUtil.writeNibble(bout, i);
         assertEquals(i, IOUtil.readNibble(bin));
         bout.reset();
         bin.reset();
      }
      print(IOUtil.writeNibble(bout, 0), buf, 0);
      bout.reset();
      print(IOUtil.writeNibble(bout, 128), buf, 128);
      bout.reset();
      print(IOUtil.writeNibble(bout, 5964), buf, 5964);
   }

   private static void print(int len, byte[] buf, int orig) {
      if (log.isDebugEnabled()) {
         final StringBuilder sb = new StringBuilder(len * 5 + 16);
         sb.append(orig).append(':');
         for (int i = 0; i < len; i++) {
            sb.append(" 0x");
            sb.append(Integer.toHexString(buf[i] & 0xff).toUpperCase());
         }
         log.debug("{}", sb);
      }
   }

   private static class MyByteArrayOutputStream extends ByteArrayOutputStream {
      private MyByteArrayOutputStream(byte[] bytes) {
         buf = bytes;
      }
   }
}
