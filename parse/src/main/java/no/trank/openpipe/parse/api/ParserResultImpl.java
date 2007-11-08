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

import java.util.Collections;
import java.util.Map;

/**
 * A basic implementation of a parser-result.
 * 
 * @version $Revision$
 */
public class ParserResultImpl implements ParserResult {
   private String title;
   private String text;
   private Map<String, String> properties = Collections.emptyMap();

   /**
    * Constructs an empty <tt>ParserResultImpl</tt>.
    */
   public ParserResultImpl() {
   }

   /**
    * Constructs a <tt>ParserResultImpl</tt> with the given text.
    */
   public ParserResultImpl(String text) {
      this.text = text;
   }

   @Override
   public String getTitle() {
      return title;
   }

   /**
    * Sets the title of the parser-result.
    * 
    * @param title the new title of the parser-result.
    */
   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   public String getText() {
      return text;
   }

   /**
    * Sets the text of the parser-result.
    * 
    * @param text the new text of the parser-result.
    */
   public void setText(String text) {
      this.text = text;
   }

   @Override
   public Map<String, String> getProperties() {
      return properties;
   }

   /**
    * Sets the properties of the parser-result.
    * 
    * @param properties the new properties of the parser-result. <tt>null</tt> will be replaced with 
    * {@link Collections#emptyMap()}.
    */
   public void setProperties(Map<String, String> properties) {
      if (properties != null) {
         this.properties = properties;
      } else {
         this.properties = Collections.emptyMap();
      }
   }
}
