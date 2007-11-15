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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class CopyField extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(CopyField.class);
   private Map<String, String> fieldNameMap;
   private boolean withAnnotations;
   private boolean overwrite = true;

   public CopyField() {
      super("CopyField");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      if (fieldNameMap != null) {
         for(Map.Entry<String, String> pair : fieldNameMap.entrySet()) {
            process(doc, pair.getKey(), pair.getValue());
         }
      }

      return PipelineStepStatus.DEFAULT;
   }

   private PipelineStepStatus process(Document doc, String fromFieldName, String toFieldName) throws PipelineException {
      List<String> values = doc.getFieldValues(fromFieldName);
    
      if (values == null || values.isEmpty()) {
         log.debug("Missing field '{}'", fromFieldName);
      } else if (overwrite || !doc.containsField(toFieldName)) {
         if (withAnnotations) {
            doc.setField(toFieldName, doc.getFields(fromFieldName));
            log.debug("Copying field '{}' to '{}', with annotations", fromFieldName, toFieldName);
         } else {
            doc.setFieldValues(toFieldName, doc.getFieldValues(fromFieldName));
            log.debug("Copying field '{}' to '{}'", fromFieldName, toFieldName);
         }
      } else {
         log.debug("Did not copy field '{}' to '{}' because overwrite was not set", fromFieldName, toFieldName);
      }

      return PipelineStepStatus.DEFAULT;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public Map<String, String> getFieldNameMap() {
      return fieldNameMap;
   }

   public void setFieldNameMap(Map<String, String> fieldNameMap) {
      this.fieldNameMap = fieldNameMap;
   }

   public boolean isWithAnnotations() {
      return withAnnotations;
   }

   public void setWithAnnotations(boolean withAnnotations) {
      this.withAnnotations = withAnnotations;
   }

   public boolean isOverwrite() {
      return overwrite;
   }

   public void setOverwrite(boolean overwrite) {
      this.overwrite = overwrite;
   }
}
