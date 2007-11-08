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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.ResolvedAnnotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision$
 */
public class Debug extends BasePipelineStep {
   private Logger log = LoggerFactory.getLogger(Debug.class);
   private Set<String> annotationTypes = null;
   private Set<String> fieldNames = null;
   private boolean showFieldValue = true;

   public Debug() {
      super("Debug");
   }

   public void setLoggerName(String loggerName) {
      log = LoggerFactory.getLogger(Debug.class.getName() + "." + loggerName);
   }

   @Override
   public PipelineStepStatus execute(Document doc) {
      if (log.isDebugEnabled()) {
         StringBuilder sb = new StringBuilder();
         for (String fieldName : doc.getFieldNames()) {
            if (fieldNames != null && !fieldNames.contains(fieldName)) {
               continue;
            }
            sb.append(fieldName);
            List<AnnotatedField> fields = doc.getFields(fieldName);
            if (showFieldValue) {
               sb.append("={");
               sb.append(doc.getFieldValues(fieldName));
               sb.append('}');
            }
            if (annotationTypes == null || !annotationTypes.isEmpty()) {
               sb.append(" Annotations{");
               buildAnnotationString(sb, fields, annotationTypes);
               sb.append("}, ");
            } else {
               sb.append(", ");
            }
         }
         if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
         }
         log.debug(sb.toString());
      }
      return PipelineStepStatus.DEFAULT;
   }

   @Override
   public void prepare() throws PipelineException {
      log.debug("prepare");
   }

   @Override
   public void finish(boolean success) throws PipelineException {
      log.debug("finish({})", success);
   }

   private static void buildAnnotationString(StringBuilder sb, List<AnnotatedField> fields, Set<String> types) {
      for (AnnotatedField field : fields) {
         for (String annotationType : types == null ? field.getAnnotationTypes() : types) {
            Iterator<ResolvedAnnotation> annotationIt = field.iterator(annotationType);
            if (annotationIt.hasNext()) {
               sb.append(annotationType).append(":[");
               while (annotationIt.hasNext()) {
                  ResolvedAnnotation annotation = annotationIt.next();
                  sb.append('\'');
                  sb.append(annotation.getValue());
                  sb.append("\',\n");
               }
               sb.setCharAt(sb.length() - 2, ']');
            }
         }
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public Set<String> getAnnotationTypes() {
      return annotationTypes;
   }

   public void setAnnotationTypes(Set<String> annotationTypes) {
      this.annotationTypes = annotationTypes;
   }

   public boolean isShowFieldValue() {
      return showFieldValue;
   }

   public void setShowFieldValue(boolean showFieldValue) {
      this.showFieldValue = showFieldValue;
   }

   public Set<String> getFieldNames() {
      return fieldNames;
   }

   public void setFieldNames(Set<String> fieldNames) {
      this.fieldNames = fieldNames;
   }
}
