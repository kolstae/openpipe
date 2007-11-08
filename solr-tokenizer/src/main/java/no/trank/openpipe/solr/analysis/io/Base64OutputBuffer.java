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

/**
 * @version $Revision$
 */
public class Base64OutputBuffer extends Base64Output {
   private static final int BUFFER_INC = 4096;
   private char[] buf;
   private int pos;

   /**
    * Creates a <tt>Base64OutputBuffer</tt> with default capacity of <tt>4096</tt>.
    */
   public Base64OutputBuffer() {
      this(BUFFER_INC);
   }

   /**
    * Creates a <tt>Base64OutputBuffer</tt> with the given capacity.
    * 
    * @param capacity the initial capacity for this stream.
    */
   public Base64OutputBuffer(int capacity) {
      buf = new char[capacity];
   }

   @Override
   public void write(final int b) throws IOException {
      ensureCapacity(pos + 2);
      writeBase64(b);
   }

   @Override
   public void write(final byte b[], final int off, final int len) throws IOException {
      ensureCapacity(pos + calcBase64Len(len));
      writeBase64(b, off, len);
   }

   @Override
   protected void writeByte(char b) {
      buf[pos++] = b;
   }

   private static int calcBase64Len(int len) {
      return ((len * 4) + 2) / 3;
   }

   private void ensureCapacity(int capacity) {
      if (capacity >= buf.length) {
         final char[] tmp = new char[Math.max(capacity, buf.length + BUFFER_INC)];
         System.arraycopy(buf, 0, tmp, 0, pos);
         buf = tmp;
      }
   }

   @Override
   public void flush() throws IOException {
      ensureCapacity(pos + (off >> 1) - 1);
      flushBase64();
   }

   @Override
   public String toString() {
      return new String(buf, 0, pos);
   }
   
   @Override
   public void reset() {
      super.reset();
      pos = 0;
   }

   public void trimToSize(int maxSize) {
      if (buf.length > maxSize) {
         buf = new char[maxSize];
      }
   }
}