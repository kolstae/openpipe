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
package no.trank.openpipe.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotEmpty;

/**
 * @version $Revision$
 */
public abstract class MultiInputOutputFieldPipelineStep extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(MultiInputOutputFieldPipelineStep.class);
   @NotEmpty
   private Map<String, String> fieldNameMap;
   private boolean skipEmptyInput;

   /**
    * Creates a step with the given name.
    *
    * @param name the name of step.
    * @param skipEmptyInput indicates whether {@link #process(Document, String, List, String)} should be skipped for
    *                       empty input fields
    *
    * @see PipelineStep#getName()
    * @see PipelineStep#setName(String)
    */
   public MultiInputOutputFieldPipelineStep(String name, boolean skipEmptyInput) {
      super(name);
      this.skipEmptyInput = skipEmptyInput;
   }

   /**
    * Executes {@link #executeInputOutputFields(Document)}.
    * 
    * @return <tt>PipelineStepStatus.DEFAULT</tt>.
    */
   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      executeInputOutputFields(doc);
      return PipelineStepStatus.DEFAULT;
   }


   /**
    * Executes {@link #process(Document, String, List, String)} for all field pairs in
    * {@link #getFieldNameMap() fieldNameMap}. Skips empty input fields if skipEmptyInput is set.
    * 
    * @param doc the document to process
    * 
    * @throws PipelineException if thrown by {@link #process(Document, String, List, String)}
    */
   protected void executeInputOutputFields(Document doc) throws PipelineException {
      for (Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
         String inputFieldName = entry.getKey();
         final List<AnnotatedField> inputFields = doc.getFields(inputFieldName);
         String outputFieldName = entry.getValue();
         
         if (!skipEmptyInput || !inputFields.isEmpty()) {
            process(doc, inputFieldName, inputFields, outputFieldName);
         } else {
            log.debug("Field '{}' is empty", entry.getKey());
         }
      }
   }

   /**
    * Processes an input/output field pair.
    * 
    * @param doc the document being processed
    * @param inputFieldName the name of the input field
    * @param inputFields the fields containing the input values
    * @param outputFieldName the name of the output field
    * @throws PipelineException if an error occurs
    * 
    * @see #executeInputOutputFields(Document)
    */
   protected abstract void process(Document doc, String inputFieldName, List<AnnotatedField> inputFields,
         String outputFieldName)
         throws PipelineException;


   /**
    * Gets whether {@link #process(Document, String, List, String)} should be skipped for empty input fields.
    * 
    * @return true if {@link #process(Document, String, List, String)} should be skipped for empty input fields,
    *         false otherwise
    */
   public boolean isSkipEmptyInput() {
      return skipEmptyInput;
   }
   
   /**
    * Gets the input/output field name map.
    *
    * @return the name map
    */
   public Map<String, String> getFieldNameMap() {
      return fieldNameMap;
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
    * Sets the input and output field for this step. Calls
    * <tt>setFieldNameMap(Collections.singletonMap(inputField, outputField))</tt>.
    * 
    * @param inputField the input-field for this step.
    */
   public void setInputOutputField(String inputField, String outputField) {
      setFieldNameMap(Collections.singletonMap(inputField, outputField));
   }
}