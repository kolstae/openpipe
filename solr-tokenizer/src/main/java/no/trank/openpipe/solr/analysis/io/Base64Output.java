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
package no.trank.openpipe.solr.analysis.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @version $Revision$
 */
public abstract class Base64Output extends OutputStream {
   protected static final char[] B64_CHARS = new char[]{
         'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
         'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
         'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
         'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
   private static final int MASK = 0x3f;
   protected int off = 2;
   private int rest;

   protected void writeBase64(byte[] b, int off, int len) throws IOException {
      int i = off;
      final int end = off + len;
      while (this.off > 2 && i < end) {
         writeBase64(b[i++] & 0xff);
      }
      final int endFast = off + end - i - (end - i) % 3;
      while (i < endFast) {
         final int b1 = b[i++] & 0xff;
         writeByte(B64_CHARS[b1 >>> 2 & MASK]);
         final int b2 = b[i++] & 0xff;
         writeByte(B64_CHARS[(b1 << 4 | b2 >>> 4) & MASK]);
         final int b3 = b[i++] & 0xff;
         writeByte(B64_CHARS[(b2 << 2 | b3 >>> 6) & MASK]);
         writeByte(B64_CHARS[b3 & MASK]);
      }
      while (i < end) {
         writeBase64(b[i++] & 0xff);
      }
   }

   protected abstract void writeByte(char b) throws IOException;

   protected void writeBase64(int b) throws IOException {
      writeByte(B64_CHARS[rest | b >>> off & MASK]);
      if (off > 4) {
         writeByte(B64_CHARS[b & MASK]);
         off = 2;
         rest = 0;
      } else {
         rest = b << 6 - off & MASK;
         off += 2;
      }
   }

   protected void flushBase64() throws IOException {
      if (off > 2) {
         writeByte(B64_CHARS[rest]);
         while (off < 8) {
            writeByte('=');
            off += 2;
         }
      }
   }

   protected void reset() {
      off = 2;
      rest = 0;
   }
   
   @Override
   public void close() throws IOException {
      flush();
   }
}
