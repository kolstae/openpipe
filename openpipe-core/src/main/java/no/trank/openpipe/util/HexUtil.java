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

/**
 * @version $Revision$
 */
public class HexUtil {
   public static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   /**
    * Converts a byte-array to a lower case hex-string.
    * 
    * @param bytes the bytes to encode.
    * 
    * @return a hex-encoded string of length <tt>bytes.length * 2</tt>.
    * 
    * @throws NullPointerException if <tt>bytes</tt> is <tt>null</tt>.
    */
   public static String toHexString(final byte[] bytes) {
      final StringBuilder buf = new StringBuilder(bytes.length * 2);
      for (byte b : bytes) {
         buf.append(HEX_CHARS[(b >> 4) & 0xf]);
         buf.append(HEX_CHARS[b & 0xf]);
      }
      return buf.toString();
   }

   /**
    * Converts a hex-encoded string to bytes.
    * 
    * @param str the string to decode.
    * 
    * @return hex-decoded bytes.
    * 
    * @throws IllegalArgumentException if <tt>str.length() % 2 != 0</tt> or character not in <tt>[0-9a-fA-F]</tt>.
    * @throws NullPointerException if <tt>str</tt> is <tt>null</tt>.
    */
   public static byte[] toBytes(final String str) {
      final int len = str.length();
      if (len % 2 != 0) {
         throw new IllegalArgumentException("String '" + str + "'.length() % 2 != 0");
      }
      final byte[] buf = new byte[len / 2];
      int idx = 0;
      for (int i = 0; i < buf.length; i++) {
         final int b1 = charToOctet(str.charAt(idx++));
         final int b2 = charToOctet(str.charAt(idx++));
         if (b1 < 0 || b2 < 0 || b1 > 0xf || b2 > 0xf) {
            throw new IllegalArgumentException("String '" + str + "' is not hex-encoded");
         }
         buf[i] = (byte) ((b1 << 4) | b2);
      }
      return buf;
   }

   private static int charToOctet(char c) {
      if (c > '9') {
         if (c > 'F') {
            return c - 'a' + 10;
         }
         return c - 'A' + 10;
      }
      return c - '0';
   }

   private HexUtil() {
   }
}
