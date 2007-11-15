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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This step offers java regex functionality. 
 * 
 * @version $Revision$
 */
public class RegexField extends BasePipelineStep {
   private static Logger log = LoggerFactory.getLogger(RegexField.class);
   
   private Map<String, String> fieldNameMap;
   private Pattern fromPattern;
   private String toPattern;
   private boolean copyOnMiss;

   public RegexField() {
      super("RegexField");
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
      List<String> outValues = new ArrayList<String>();
    
      if (values == null || values.isEmpty()) {
         log.debug("Missing field '{}'", fromFieldName);
      } else {
         for (String value : values) {
            Matcher m = fromPattern.matcher(value);
            if (m.find()) {
               log.debug("Field '{}' matches", fromFieldName);
               outValues.add(m.replaceAll(toPattern));
            } else {
               log.debug("Field '{}' does not match", fromFieldName);
               if (copyOnMiss) {
                  outValues.add(value);
               }
            }
         }
      }
      
      if (outValues.isEmpty()) {
         doc.removeField(toFieldName);
      } else {
         doc.setFieldValues(toFieldName, outValues);
      }

      return PipelineStepStatus.DEFAULT;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public String getFromPattern() {
      return fromPattern != null ? fromPattern.pattern() : null;
   }

   /**
    * Compiles the regex pattern used for matching against the input field values.
    * Note that {@link Matcher#find()} is called during the matching process, to allow for replace all effects. 
    * 
    * @param fromPattern the pattern to be compiled
    */
   public void setFromPattern(String fromPattern) {
      this.fromPattern = Pattern.compile(fromPattern);
   }

   public String getToPattern() {
      return toPattern;
   }

   /**
    * Sets the pattern that is applied when producing the output field values through calls to
    * {@link Matcher#replaceAll(String)}.
    * 
    * @param toPattern the output pattern
    */
   public void setToPattern(String toPattern) {
      this.toPattern = toPattern;
   }

   public boolean isCopyOnMiss() {
      return copyOnMiss;
   }

   /**
    * Specifies whether the input field value should be copied to the output field if the input field value
    * does not match the from pattern. 
    * 
    * @param copyOnMiss
    */
   public void setCopyOnMiss(boolean copyOnMiss) {
      this.copyOnMiss = copyOnMiss;
   }

   public Map<String, String> getFieldNameMap() {
      return fieldNameMap;
   }

   /**
    * Sets the input/output field names.
    * 
    * @param fieldNameMap
    */
   public void setFieldNameMap(Map<String, String> fieldNameMap) {
      this.fieldNameMap = fieldNameMap;
   }
}