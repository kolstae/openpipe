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
package no.trank.openpipe.parse.api;

import java.io.IOException;
import java.io.InputStream;

import no.trank.openpipe.api.document.RawData;

/**
 * A <tt>ParseData</tt> implementation that wraps a {@link RawData}.
 * 
 * @version $Revision$
 */
public class PipelineParseData implements ParseData {
   private final RawData data;
   private final boolean includeProperties;
   private final String fileName;

   /**
    * Constructs a <tt>PipelineParseData</tt> with the given values. 
    * 
    * @param data the raw data to parse.
    * @param includeProperties whether properties are of interest.
    * @param fileName the filename of the parse-data.
    * 
    * @throws NullPointerException if <tt>data</tt> is <tt>null</tt>.
    * 
    * @see ParseData#includeProperties()
    */
   public PipelineParseData(RawData data, boolean includeProperties, String fileName) {
      this.data = data;
      this.includeProperties = includeProperties;
      this.fileName = fileName;
   }

   /**
    * {@inheritDoc}
    * This implementation delegates to {@link RawData#getInputStream()}.
    */
   @Override
   public InputStream getInputStream() throws IOException {
      return data.getInputStream();
   }

   /**
    * {@inheritDoc}
    * This implementation delegates to {@link RawData#getLength()}.
    */
   @Override
   public int getLength() {
      return data.getLength();
   }

   @Override
   public boolean includeProperties() {
      return includeProperties;
   }

   @Override
   public String getFileName() {
      return fileName;
   }
}
