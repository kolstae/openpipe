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
 * A basic implementation of {@link Annotation}.
 * 
 * @version $Revision$
 */
public class BaseAnnotation implements Annotation {
   private int startPos;
   private int endPos;

   /**
    * Constructs a <tt>BaseAnnotation</tt>.
    */
   public BaseAnnotation() {
   }

   /**
    * Constructs a <tt>BaseAnnotation</tt> with the given start and end positions.
    */
   public BaseAnnotation(int startPos, int endPos) {
      this.startPos = startPos;
      this.endPos = endPos;
   }

   @Override
   public int getStartPos() {
      return startPos;
   }

   /**
    * Sets the start position of this annotation.
    * 
    * @param startPos the start position of this annotation.
    * 
    * @see Annotation#getStartPos()
    */
   public void setStartPos(int startPos) {
      this.startPos = startPos;
   }

   @Override
   public int getEndPos() {
      return endPos;
   }

   /**
    * Sets the end position (exclusive) of this annotation.
    * 
    * @param endPos the end position (exclusive) of this annotation.
    * 
    * @see Annotation#getEndPos()
    */
   public void setEndPos(int endPos) {
      this.endPos = endPos;
   }

   @Override
   public String toString() {
      return "BaseAnnotation{" +
            "startPos=" + startPos +
            ", endPos=" + endPos +
            '}';
   }
}
