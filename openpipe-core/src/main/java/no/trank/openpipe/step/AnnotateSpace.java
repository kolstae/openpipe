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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.BaseAnnotation;
import no.trank.openpipe.api.document.Document;

/**
 * This step annotates occurences of single space characters.
 * 
 * @version $Revision$
 */
public class AnnotateSpace extends MultiInputFieldPipelineStep {
   private static final Logger log = LoggerFactory.getLogger(AnnotateSpace.class);

   public AnnotateSpace() {
      super("AnnotateSpace");
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      for (AnnotatedField fieldValue : fieldValues) {
         process(fieldName, fieldValue);
      }
   }

   private static void process(String fieldName, AnnotatedField field) {
      final String text = field.getValue();
      if (text == null) {
         log.debug("Field '{}' - null", fieldName);
      } else {
         List<Annotation> annotations = new ArrayList<Annotation>();
         
         int i = -1;
         while((i = text.indexOf(' ', i+1)) != -1) {
            annotations.add(new BaseAnnotation(i, i+1));
         }
         
         log.debug("Field '{}'  space annotations: {}", fieldName, annotations.size());
         field.add("space", annotations);
      }
   }

}