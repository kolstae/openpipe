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

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This step converts input text into uppercase.
 * 
 * @version $Revision$
 */
public class Uppercase extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(Uppercase.class);
   private Map<String, String> fieldNameMap;

   public Uppercase() {
      super("Uppercase");
   }

   @Override
   public PipelineStepStatus execute(Document doc) {
      if (fieldNameMap != null) {
         for(Map.Entry<String, String> pair : fieldNameMap.entrySet()) {
            process(doc, pair.getKey(), pair.getValue());
         }
      }

      return PipelineStepStatus.DEFAULT;
   }

   private static void process(Document doc, String input, String output) {
      if (!doc.containsField(input)) {
         log.debug("Field '{}' - null; Output field: '{}'", input, output);
      } else {
         final List<String> values = doc.getFieldValues(input);
         final ArrayList<String> newValues = new ArrayList<String>(values.size());
         for (String text : values) {
            log.debug("Field '{}' length: {}; Output field: '{}'", new Object[] { input, text.length(), output } );
            newValues.add(text.toUpperCase());
         }
         doc.setFieldValues(output, newValues);
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   /**
    * Sets the names of the input/output field pairs.
    * 
    * @param fieldNameMap
    */
   public void setFieldNameMap(Map<String, String> fieldNameMap) {
      this.fieldNameMap = fieldNameMap;
   }

   /**
    * Returns the names of the input/output field pairs.
    * 
    * @return the name map
    */
   public Map<String, String> getFieldNameMap() {
      return fieldNameMap;
   }
}