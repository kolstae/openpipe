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
public class Base64OutputStream extends Base64Output {
   private final OutputStream out;
   private final byte[] buf;
   private int pos;

   /**
    * Creates a <tt>Base64OutputStream</tt> with a buffer size of <tt>4096</tt>.
    * 
    * @param out the outputStream to write to.
    */
   public Base64OutputStream(OutputStream out) {
      this(out, 4096);
   }

   /**
    * Creates a <tt>Base64OutputStream</tt> with the given buffer size.
    *
    * @param out the outputStream to write to.
    * @param bufferSize the buffer size for this stream.
    */
   public Base64OutputStream(OutputStream out, int bufferSize) {
      if (out == null) {
         throw new NullPointerException("OutputStream == null");
      }
      if (bufferSize < 1) {
         throw new IllegalArgumentException("bufferSize must be > 0 was" + bufferSize);
      }
      this.out = out;
      buf = new byte[bufferSize];
   }

   @Override
   public void write(final int b) throws IOException {
      writeBase64(b);
   }

   @Override
   public void write(final byte b[], final int off, final int len) throws IOException {
      writeBase64(b, off, len);
   }

   @Override
   protected void writeByte(char b) throws IOException {
      if (pos >= buf.length) {
         out.write(buf);
         pos = 0;
      }
      buf[pos++] = (byte) b;
   }

   @Override
   public void flush() throws IOException {
      flushBase64();
      if (pos > 0) {
         out.write(buf, 0, pos);
         pos = 0;
      }
      out.flush();
   }

   @Override
   public void close() throws IOException {
      try {
         super.close();
      } finally {
         out.close();
      }
   }
}