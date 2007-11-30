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
package no.trank.openpipe.wikipedia.producer;

import java.io.InputStream;
import java.io.IOException;

/**
 * Strips bytes from the beginning of a stream.
 *
 * @version $Revision$
 */
public class InputStreamPrefixStripper extends InputStream {
   private InputStream originalStream;
   private boolean inPrefix = true;
   private int position = 0;
   private final byte[] stripBytes;

   public InputStreamPrefixStripper(InputStream inputStream, byte[] stripBytes) {
      this.stripBytes = stripBytes;
      this.originalStream = inputStream;
   }

   @Override
   public int read() throws IOException {
      if (inPrefix) {
         int originalByte = originalStream.read();
         while (stripBytes.length > position && originalByte == stripBytes[position++]) {
            originalByte = originalStream.read();
         }
         inPrefix = false;
         return originalByte;
      } else {
         return originalStream.read();
      }
   }
}
