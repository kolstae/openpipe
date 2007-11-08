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