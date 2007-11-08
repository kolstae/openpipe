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

/**
 * An interface representing data to be parsed.
 * 
 * @version $Revision$
 */
public interface ParseData {

   /**
    * Gets a stream for the data.
    * 
    * @return a stream for the data.
    * 
    * @throws IOException if an I/O-error occured.
    */
   InputStream getInputStream() throws IOException;

   /**
    * Gets the length of the stream returned by {@link #getInputStream()}.
    * 
    * @return the number of bytes.
    */
   int getLength();

   /**
    * Tells whether properties of a parsed document is of interest.
    * 
    * @return <tt>true</tt> if properties of a document is of interest, otherwise <tt>false</tt>.
    */
   boolean includeProperties();

   /**
    * Gets the filename of the data.
    * 
    * @return the filename of the data.
    */
   String getFileName();
}
