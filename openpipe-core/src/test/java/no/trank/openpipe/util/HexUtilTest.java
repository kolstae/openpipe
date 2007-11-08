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
package no.trank.openpipe.util;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class HexUtilTest extends TestCase {
   private static final byte[] BYTES = new byte[256];
   private static final String HEX = generateHEX();

   public void testToHexString() throws Exception {
      assertEquals(HEX.toLowerCase(), HexUtil.toHexString(BYTES));
   }

   public void testToBytes() throws Exception {
      assertTrue(Arrays.equals(BYTES, HexUtil.toBytes(HEX)));
      try {
         HexUtil.toBytes("0Af0Gfag9t");
         fail("Should throw IllegalArgumentException");
      } catch (Exception e) {
         // Success
      }
   }
   
   private static String generateHEX() {
      final StringBuilder buf = new StringBuilder(BYTES.length * 2);
      int c = 0;
      for (int i = 0; i < BYTES.length; i++) {
         BYTES[i] = (byte) i;
         if (i < 0x10) {
            buf.append('0');
         }
         final String s = Integer.toHexString(i);
         buf.append(++c % 3 == 0 ? s.toUpperCase() : s);
      }
      return buf.toString();
   }
}