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
package no.trank.openpipe.solr.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
      final byte[] buf = new byte[5];
      final ByteArrayInputStream bin = new ByteArrayInputStream(buf);
      final MyByteArrayOutputStream bout = new MyByteArrayOutputStream(buf);
      bin.mark(5);
      for (int i = 0; i < 50000; i++) {
         testValue(bout, bin, i * 42949);
      }
      testAndPrint(bout, bin, buf, 0);
      testAndPrint(bout, bin, buf, 128);
      testAndPrint(bout, bin, buf, 5964);
      testAndPrint(bout, bin, buf, Integer.MAX_VALUE);
   }

   private static void testAndPrint(MyByteArrayOutputStream bout, ByteArrayInputStream bin, byte[] buf, int value) 
         throws IOException {
      final int len = testValue(bout, bin, value);
      if (log.isDebugEnabled()) {
         final StringBuilder sb = new StringBuilder(len * 5 + 16);
         sb.append(value).append(':');
         for (int i = 0; i < len; i++) {
            sb.append(" 0x");
            sb.append(Integer.toHexString(buf[i] & 0xff).toUpperCase());
         }
         log.debug("{}", sb);
      }
   }

   private static int testValue(MyByteArrayOutputStream bout, ByteArrayInputStream bin, int value) throws IOException {
      final int len = IOUtil.writeNibble(bout, value);
      assertEquals(value, IOUtil.readNibble(bin));
      bout.reset();
      bin.reset();
      return len;
   }

   private static class MyByteArrayOutputStream extends ByteArrayOutputStream {
      private MyByteArrayOutputStream(byte[] bytes) {
         buf = bytes;
      }
   }
}
