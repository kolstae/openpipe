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
 * A basic implementation of {@link ResolvedAnnotation}.
 * 
 * @version $Revision$
 */
public class BaseResolvedAnnotation extends BaseAnnotation implements ResolvedAnnotation {
   private String fieldValue;
   private String value;

   /**
    * Constructs a <tt>BaseResolvedAnnotation</tt> from the given annotation and field-value.
    * <p/>
    * Uses <tt>fieldValue.substring(getStartPos(), getEndPos())</tt> to find its value
    * 
    * @param annotation the annotation to resove.
    * @param fieldValue the value to resolve from.
    */
   public BaseResolvedAnnotation(Annotation annotation, String fieldValue) {
      super(annotation.getStartPos(), annotation.getEndPos());
      this.fieldValue = fieldValue;
   }

   /**
    * {@inheritDoc}
    * <p/>
    * On first access the value is resolved as described {@link #BaseResolvedAnnotation(Annotation, String) here}. 
    * 
    * @see #BaseResolvedAnnotation(Annotation, String) 
    */
   @Override
   public String getValue() {
      if (value == null) {
         value = fieldValue.substring(getStartPos(), getEndPos());
         fieldValue = null;
      }
      return value;
   }

   @Override
   public String toString() {
      return "BaseResolvedAnnotation{" +
            "value='" + getValue() + '\'' +
            '}';
   }
}
