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

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotNull;

/**

 * @version $Revision$
 */
public class ONLPTokenizer extends MultiInputFieldPipelineStep {
   public static final String TYPE_TOKENIZE = "opennlp.tokenize";
   @NotNull
   private Tokenizer tokenizer;

   public ONLPTokenizer() {
      super("Open NLP Tokenizer");
   }

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      for (AnnotatedField fieldValue : fieldValues) {
         fieldValue.add(TYPE_TOKENIZE, buildAnn(tokenizer.tokenizePos(fieldValue.getValue())));
      }
   }

   private static List<Annotation> buildAnn(Span[] spans) {
      final List<Annotation> list = new ArrayList<Annotation>(spans.length);
      for (Span span : spans) {
         list.add(new SpanAnnotation(span));
      }
      return list;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public Tokenizer getTokenizer() {
      return tokenizer;
   }

   public void setTokenizer(Tokenizer tokenizer) {
      this.tokenizer = tokenizer;
   }

   private static final class SpanAnnotation implements Annotation {
      private final Span span;

      public SpanAnnotation(Span span) {
         this.span = span;
      }

      @Override
      public int getStartPos() {
         return span.getStart();
      }

      @Override
      public int getEndPos() {
         return span.getEnd();
      }
   }
}
