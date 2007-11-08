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

import java.util.Map;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class SetField extends BasePipelineStep {
   private Map<String, String> fieldValueMap;
   private boolean overwrite = true;

   public SetField() {
      super("SetField");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      for (Map.Entry<String, String> entry : fieldValueMap.entrySet()) {
         if (overwrite || !doc.containsField(entry.getKey())) {
            doc.setFieldValue(entry.getKey(), entry.getValue());
         }
      }
      return PipelineStepStatus.DEFAULT;
   }

   public Map<String, String> getFieldValueMap() {
      return fieldValueMap;
   }

   public void setFieldValueMap(Map<String, String> fieldValueMap) {
      this.fieldValueMap = fieldValueMap;
   }

   public boolean isOverwrite() {
      return overwrite;
   }

   public void setOverwrite(boolean overwrite) {
      this.overwrite = overwrite;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }
}
