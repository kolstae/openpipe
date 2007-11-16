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

import no.trank.openpipe.api.MultiInputOutputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This step converts input text into uppercase.
 * 
 * @version $Revision$
 */
public class Uppercase extends MultiInputOutputFieldPipelineStep {
   private static final Logger log = LoggerFactory.getLogger(Uppercase.class);

   public Uppercase() {
      super("Uppercase", false);
   }

   @Override
   protected void process(Document doc, String inputFieldName, List<AnnotatedField> inputFields, String outputFieldName)
         throws PipelineException {
      if (inputFields.isEmpty()) {
         doc.removeField(outputFieldName);
      }
      else {
         final ArrayList<String> outValues = new ArrayList<String>(inputFields.size());
         
         for (AnnotatedField field : inputFields) {
            final String text = field.getValue();
            log.debug("Field '{}' length: {}; Output field: '{}'",
                      new Object[] { inputFieldName, text.length(), outputFieldName } );
            outValues.add(text.toUpperCase());
         }
         doc.setFieldValues(outputFieldName, outValues);
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }
}