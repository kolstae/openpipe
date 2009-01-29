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
package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.ResolvedAnnotation;
import no.trank.openpipe.config.annotation.NotEmpty;

/**
 * @version $Revision$
 */
public class AnnotationToField extends BasePipelineStep {
   @NotEmpty
   private String fromFieldName;
   @NotEmpty
   private String annotationType;
   @NotEmpty
   private String toFieldName;
   private boolean failOnEmpty;

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final List<String> newValues = buildNewValues(doc.getFields(fromFieldName));
      if (!newValues.isEmpty()) {
         doc.setFieldValues(toFieldName, newValues);
      } else if (failOnEmpty) {
         throw new PipelineException("No annotations found for '" + fromFieldName + "':'" + annotationType + "'");
      }

      return PipelineStepStatus.DEFAULT;
   }

   private List<String> buildNewValues(List<AnnotatedField> list) {
      if (!list.isEmpty()) {
         final ArrayList<String> newValues = new ArrayList<String>();
         for (AnnotatedField annField : list) {
            final ListIterator<ResolvedAnnotation> it = annField.iterator(annotationType);
            while (it.hasNext()) {
               newValues.add(it.next().getValue());
            }
         }
         return newValues;
      }
      return Collections.emptyList();
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public String getFromFieldName() {
      return fromFieldName;
   }

   public void setFromFieldName(String fromFieldName) {
      this.fromFieldName = fromFieldName;
   }

   public String getToFieldName() {
      return toFieldName;
   }

   public void setToFieldName(String toFieldName) {
      this.toFieldName = toFieldName;
   }

   public String getAnnotationType() {
      return annotationType;
   }

   public void setAnnotationType(String annotationType) {
      this.annotationType = annotationType;
   }

   public boolean isFailOnEmpty() {
      return failOnEmpty;
   }

   public void setFailOnEmpty(boolean failOnEmpty) {
      this.failOnEmpty = failOnEmpty;
   }
}