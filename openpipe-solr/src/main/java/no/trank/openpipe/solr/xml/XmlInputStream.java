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
package no.trank.openpipe.solr.xml;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <code>XmlInputStream</code> is a {@link FilterInputStream} implementation that trims leading whitespace characters.
 * 
 * @version $Revision$
 */
public class XmlInputStream extends FilterInputStream {
   private boolean skipping;
   
   public XmlInputStream(InputStream inputStream) {
      super(inputStream); 
      skipping = true;
   }

   @Override
   public int read() throws IOException {
      int ret = in.read();
      
      while(skipping && ret >= 0) {
         if(ret != ' ' && ret != '\t' && ret != '\n') {
            skipping = false;
         }
         else {
            ret = in.read();
         }
      }
      
      if(ret < 0) {
         skipping = false;
      }
      
      return ret;
   }
   
   @Override
   public int read(byte b[], int off, int len) throws IOException {
      boolean one = false;
      
      while(skipping) {
         int tmp = read();
         if(!skipping) {
            b[off++] = (byte)tmp;
            --len;
            one = true;
         }
      }
      
      int ret = in.read(b, off, len);
      
      if(ret == -1) {
         return one ? 1 : -1;
      }
      else {
         return one ? ret + 1 : ret;
      }
   }
   
   @Override
   public long skip(long n) throws IOException {
      long ret = 0L;
      while(--n >= 0) {
         if(read() >= 0) {
            ++ret;
         }
         else {
            break;
         }
      }
      
      return ret;
   }
}