/*
 * Copyright 2008 T-Rank AS
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
package no.trank.openpipe.api.document;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 * @version $Revision$
 */
public class ByteArrayRawData implements RawData {
   private byte[] bytes;

   public ByteArrayRawData(byte[] bytes) {
      this.bytes = bytes;
   }

   @Override
   public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(bytes);
   }

   @Override
   public int getLength() {
      return bytes.length;
   }

   @Override
   public void release() {
      bytes = null;
   }

   @Override
   public boolean isReleased() {
      return bytes == null;
   }
}

