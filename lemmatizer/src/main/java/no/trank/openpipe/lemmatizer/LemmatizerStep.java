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
package no.trank.openpipe.lemmatizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import no.trank.openpipe.api.MultiInputOutputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.PreResolvedAnnotation;
import no.trank.openpipe.api.document.ResolvedAnnotation;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;
import no.trank.openpipe.lemmatizer.model.LemmatizeModel;

/**
 * A step for lemmatization by expansion.
 * <p/>
 * This step needs a pre tokenized field, with annotation type {@link #getInputAnnotationType() inputAnnotationType}.
 * <p/>
 * Output from this step is {@link PreResolvedAnnotation}'s with {@link PreResolvedAnnotation#getStartPos() startPos}
 * and {@link PreResolvedAnnotation#getEndPos() endPos} same as the original {@link Annotation}.
 *
 * @version $Revision$
 */
public class LemmatizerStep extends MultiInputOutputFieldPipelineStep {
   @NotEmpty
   private String inputAnnotationType = "word";
   @NotEmpty
   private String outputAnnotationType = "lemma";
   @NotNull
   private LemmatizeModel model;
   private boolean overwriteOutputAnnotation;
   private boolean includeInputAnnotation;

   /**
    * {@inheritDoc}
    */
   public LemmatizerStep() {
      super("Lemmatizer", true);
   }

   @Override
   protected void process(Document doc, String inputFieldName, List<AnnotatedField> inputFields, String outputFieldName)
         throws PipelineException {
      for (AnnotatedField field : inputFields) {
         final ListIterator<ResolvedAnnotation> it = field.iterator(inputAnnotationType);
         if (it.hasNext()) {
            final List<ResolvedAnnotation> anns = new ArrayList<ResolvedAnnotation>();
            while (it.hasNext()) {
               addAnnotations(it.next(), anns);
            }
            if (!overwriteOutputAnnotation) {
               field.add(outputAnnotationType, anns);
            } else {
               field.set(outputAnnotationType, anns);
            }
         }
      }
   }

   private void addAnnotations(ResolvedAnnotation term, List<ResolvedAnnotation> anns) {
      final Iterator<String> it = model.get(term.getValue());
      if (it.hasNext()) {
         while (it.hasNext()) {
            anns.add(new PreResolvedAnnotation(term.getStartPos(), term.getEndPos(), it.next()));
         }
      } else if (includeInputAnnotation) {
         anns.add(term);
      }
   }

   @Override
   public void prepare() throws PipelineException {
      super.prepare();
      model.reset();
   }

   @Override
   public void finish(boolean success) throws PipelineException {
      model.log();
      super.finish(success);
   }

   /**
    * Gets the model used for lemmatization.
    *
    * @return the model used for lemmatization.
    */
   public LemmatizeModel getModel() {
      return model;
   }

   /**
    * Sets the model used for lemmatization.
    *
    * @param model the model used for lemmatization. <i>Cannot</i> be <tt>null</tt>.
    */
   public void setModel(LemmatizeModel model) {
      this.model = model;
   }

   /**
    * Gets the type of annotations used for input. <br/>
    * Default value is <tt>&quot;word&quot;</tt>.
    *
    * @return the type of annotations used for input.
    */
   public String getInputAnnotationType() {
      return inputAnnotationType;
   }

   /**
    * Sets the type of annotations used for input.
    *
    * @param inputAnnotationType the type of annotations used for input.
    *
    * @see #getInputAnnotationType()
    */
   public void setInputAnnotationType(String inputAnnotationType) {
      this.inputAnnotationType = inputAnnotationType;
   }

   /**
    * Gets the type of annotations used for output. <br/>
    * Default value is <tt>&quot;lemma&quot;</tt>.
    *
    * @return the type of annotations used for output.
    */
   public String getOutputAnnotationType() {
      return outputAnnotationType;
   }

   /**
    * Sets the type of annotations used for output.
    *
    * @param outputAnnotationType the type of annotations used for output.
    *
    * @see #getOutputAnnotationType()
    */
   public void setOutputAnnotationType(String outputAnnotationType) {
      this.outputAnnotationType = outputAnnotationType;
   }

   public void setOverwriteOutputAnnotation(boolean overwriteOutputAnnotation) {
      this.overwriteOutputAnnotation = overwriteOutputAnnotation;
   }

   public void setIncludeInputAnnotation(boolean includeInputAnnotation) {
      this.includeInputAnnotation = includeInputAnnotation;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }
}
