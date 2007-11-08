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
package no.trank.openpipe.api.document;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface representing the raw-data of a document.
 *
 * @version $Revision$
 */
public interface RawData {

   /**
    * Gets a stream of the data.
    *
    * @return a stream of the data.
    *
    * @throws IOException if an I/O-error occured.
    */
   InputStream getInputStream() throws IOException;

   /**
    * Gets the length of the data.
    * 
    * @return the length of the data.
    */
   int getLength();

   /**
    * Releases the resources of this instance.
    */
   void release();

   /**
    * Gets whether {@link #release()} has been called for this instance.
    * 
    * @return <tt>true</tt> if {@link #release()} has been called for this instance, otherwise <tt>false</tt>.
    */
   boolean isReleased();
}
