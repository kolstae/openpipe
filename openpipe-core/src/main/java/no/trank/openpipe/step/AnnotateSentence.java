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
import java.util.Arrays;
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
 * @version $Revision$
 */
public class AnnotateSentence extends MultiInputFieldPipelineStep {
   private static final Logger log = LoggerFactory.getLogger(AnnotateSpace.class);
   public static final String SENTENCE = "sentence";
   private static final char[] CHARS_SENT = new char[] {'.', '!', '?'};
   private static final char[] CHARS_WS = new char[] {' ', '"', '\r', '\n', '\t'};

   public AnnotateSentence() {
      super("AnnotateSentence");
   }

   static {
      Arrays.sort(CHARS_SENT);
      Arrays.sort(CHARS_WS);
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
         
         int from = -1;
         for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (from < 0) {
               if (!isWhiteSpace(c)) {
                  from = i;
               } else {
                  continue;
               }
            }
            if (Arrays.binarySearch(CHARS_SENT, c) >= 0 && from < i - 1) {
               if (i < text.length() - 1 && isWhiteSpace(text.charAt(i + 1))) {
                  annotations.add(new BaseAnnotation(from, i));
                  from = -1;
               }
            }
         }
         if (from >= 0 && from < text.length() - 1) {
            annotations.add(new BaseAnnotation(from, text.length()));
         }
         
         log.debug("Field '{}'  sentence annotations: {}", fieldName, annotations.size());
         field.add(SENTENCE, annotations);
      }
   }

   private static boolean isWhiteSpace(char c) {
      return Arrays.binarySearch(CHARS_WS, c) >= 0;
   }
}