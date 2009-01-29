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
package no.trank.openpipe.opennlp.step;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetector;

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.BaseAnnotation;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotNull;

/**

 * @version $Revision$
 */
public class ONLPSentenceDetector extends MultiInputFieldPipelineStep {
   public static final String TYPE_SENTENCE = "opennlp.sentence";
   @NotNull
   private SentenceDetector detector;

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      for (AnnotatedField fieldValue : fieldValues) {
         fieldValue.add(TYPE_SENTENCE, buildAnn(detector.sentPosDetect(fieldValue.getValue())));
      }
   }

   private static List<Annotation> buildAnn(int[] offsets) {
      final List<Annotation> list = new ArrayList<Annotation>(offsets.length);
      int lastOffset = 0;
      for (int offset : offsets) {
         list.add(new BaseAnnotation(lastOffset, offset));
         lastOffset = offset;
      }
      return list;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public SentenceDetector getDetector() {
      return detector;
   }

   public void setDetector(SentenceDetector detector) {
      this.detector = detector;
   }
}