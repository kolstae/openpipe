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

import java.util.Map;

/**
 * An interface representing a parser-result.
 * 
 * @version $Revision$
 */
public interface ParserResult {

   /**
    * Gets the title of the document, if any.
    * 
    * @return the title of the document.
    */
   String getTitle();

   /**
    * Gets the text of the document.
    * 
    * @return the text of the document.
    */
   String getText();

   /**
    * Gets the properties of the document, if any.
    * 
    * @return the properties of the document. Must <i>not</i> be <tt>null</tt>.
    */
   Map<String, String> getProperties();
}
