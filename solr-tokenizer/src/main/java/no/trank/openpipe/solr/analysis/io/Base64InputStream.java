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
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * @version $Revision$
 */
public class Base64InputStream extends InputStream {
   private static final int[] B64_VALS = new int['z' + 1];
   private static final int MASK = 0x3f;
   private final char[] buf = new char[4096];
   private final Reader in;
   private int pos;
   private int size;
   private int off = 8;
   private int rest;
   private boolean eof;

   public Base64InputStream(Reader in) {
      this.in = in;
   }

   public Base64InputStream(String externalVal) {
      this(new StringReader(externalVal));
   }

   @Override
   public int read() throws IOException {
      final boolean need2Bytes = off > 6;
      if (!fillBuffer(need2Bytes ? 2 : 1)) {
         return -1;
      }
      if (need2Bytes) {
         final int b = decode(buf[pos++]);
         if (eof) {
            return -1;
         }
         rest = b << 2;
         off = 2;
      }
      final int b = decode(buf[pos++]);
      if (eof) {
         return -1;
      }
      final int result = rest | (b >> (6 - off));
      rest = ((b << off) & MASK) << 2;
      off += 2;
      return result;
   }

   private boolean fillBuffer(int wantedSize) throws IOException {
      if (eof) {
         return false;
      }
      if (pos + wantedSize > size) {
         if (pos < size) {
            size -= pos;
            System.arraycopy(buf, pos, buf, 0, size);
         } else {
            size = 0;
         }
         pos = 0;
         final int len = in.read(buf, pos, buf.length - size);
         if (len < 0 && size == 0) {
            size = -1;
            eof = true;
         } else {
            size += len;
         }
      }
      return pos + wantedSize <= size;
   }

   private int decode(int b) {
      if (b >= 0 && b < B64_VALS.length) {
         return B64_VALS[b];
      }
      eof = true;
      return -1;
   }

   @Override
   public long skip(final long n) throws IOException {
      long skipped = 0;
      while (skipped < n && read() >= 0) {
         skipped++;
      }
      return skipped;
   }

   @Override
   public void close() throws IOException {
      in.close();
   }
   
   static {
      for (int i = 0; i < B64_VALS.length; i++) {
         B64_VALS[i] = -1;
      }
      for (int i = 0; i < Base64OutputStream.B64_CHARS.length; i++) {
         B64_VALS[Base64OutputStream.B64_CHARS[i]] = i;
      }
   }
}
