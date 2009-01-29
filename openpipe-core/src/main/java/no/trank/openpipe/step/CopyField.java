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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.MultiInputOutputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;

/**
 * This step copies values from fields to other fields.
 * 
 * <p><ul>
 *    <li>The property <code>withAnnotations</code> indicates whether annotations will be copied.</li>
 *    <li>The property <code>overwrite</code> indicates whether non-empty output fields will be overwritten.</li>
 * </ul>
 *
 * @version $Revision$
 */
public class CopyField extends MultiInputOutputFieldPipelineStep {
   private static final Logger log = LoggerFactory.getLogger(CopyField.class);
   private boolean withAnnotations;
   private boolean overwrite = true;

   public CopyField() {
      super(false);
   }

   @Override
   protected void process(Document doc, String inputFieldName, List<AnnotatedField> inputFields, String outputFieldName)
         throws PipelineException {
      if (overwrite || !doc.containsField(outputFieldName)) {
         if (inputFields.isEmpty()) {
            log.debug("Missing field '{}'", inputFieldName);
         }
         
         if (withAnnotations) {
            doc.setField(outputFieldName, inputFields);
            log.debug("Copying field '{}' to '{}', with annotations", inputFieldName, outputFieldName);
         } else {
            doc.setFieldValues(outputFieldName, doc.getFieldValues(inputFieldName));
            log.debug("Copying field '{}' to '{}'", inputFieldName, outputFieldName);
         }
      } else {
         log.debug("Did not copy field '{}' to '{}' because overwrite was not set", inputFieldName, outputFieldName);
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   /**
    * Gets whether any annotations associated with the input fields will be copied to the output fields.
    *
    * @return <code>true</code> if the annotations will be copied, <code>false</code> otherwise
    */
   public boolean isWithAnnotations() {
      return withAnnotations;
   }

   /**
    * Sets whether any annotations associated with the input fields will be copied to the output fields.
    *
    * @param withAnnotations
    */
   public void setWithAnnotations(boolean withAnnotations) {
      this.withAnnotations = withAnnotations;
   }

   /**
    * Gets whether the output fields will be overwritten if they already exist.
    *
    * @return <code>true</code> if the output fields will be overwritten, <code>false</code> otherwise
    */
   public boolean isOverwrite() {
      return overwrite;
   }

   /**
    * Sets whether the output fields will be overwritten if they already exist.
    *
    * @param overwrite
    */
   public void setOverwrite(boolean overwrite) {
      this.overwrite = overwrite;
   }
}