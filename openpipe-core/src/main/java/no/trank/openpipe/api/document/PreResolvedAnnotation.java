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

/**
 * A basic implementation of {@link ResolvedAnnotation} with a pre-resolved value.
 * 
 * @version $Revision$
 */
public class PreResolvedAnnotation extends BaseAnnotation implements ResolvedAnnotation {
   private final String value;

   /**
    * Constructs a <tt>PreResolvedAnnotation</tt> with a given pre-resolved value and <tt>startPos = endPos = 0</tt>.
    * 
    * @param value the pre-resolved value.
    */
   public PreResolvedAnnotation(String value) {
      this(0, 0, value);
   }

   /**
    * Constructs a <tt>PreResolvedAnnotation</tt> with a given pre-resolved value.
    * 
    * @param value the pre-resolved value.
    */
   public PreResolvedAnnotation(int startPos, int endPos, String value) {
      super(startPos, endPos);
      this.value = value;
   }

   @Override
   public String getValue() {
      return value;
   }
}
