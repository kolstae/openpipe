package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.ResolvedAnnotation;

/**
 * @version $Revision$
 */
public class AnnotationToField extends BasePipelineStep {
   private String fromFieldName;
   private String annotationType;
   private String toFieldName;
   private boolean failOnEmpty;

   public AnnotationToField() {
      super("AnnotationToField");
   }

   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final List<String> newValues = buildNewValues(doc.getFields(fromFieldName));
      if (!newValues.isEmpty()) {
         doc.setFieldValues(toFieldName, newValues);
      } else if (failOnEmpty) {
         throw new PipelineException("No annotations found for '" + fromFieldName + "':'" + annotationType + "'");
      }

      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   private List<String> buildNewValues(List<AnnotatedField> list) {
      if (!list.isEmpty()) {
         final ArrayList<String> newValues = new ArrayList<String>();
         for (AnnotatedField annField : list) {
            final ListIterator<ResolvedAnnotation> it = annField.iterator(annotationType);
            while (it.hasNext()) {
               newValues.add(it.next().getValue());
            }
         }
         return newValues;
      }
      return Collections.emptyList();
   }

   @Override
   public void prepare() throws PipelineException {
      if (fromFieldName == null || annotationType == null || toFieldName == null) {
         throw new PipelineException("Field names / annotation type not configured");
      }
   }

   public String getRevision() {
      return "$Revision$";
   }

   public String getFromFieldName() {
      return fromFieldName;
   }

   public void setFromFieldName(String fromFieldName) {
      this.fromFieldName = fromFieldName;
   }

   public String getToFieldName() {
      return toFieldName;
   }

   public void setToFieldName(String toFieldName) {
      this.toFieldName = toFieldName;
   }

   public String getAnnotationType() {
      return annotationType;
   }

   public void setAnnotationType(String annotationType) {
      this.annotationType = annotationType;
   }

   public boolean isFailOnEmpty() {
      return failOnEmpty;
   }

   public void setFailOnEmpty(boolean failOnEmpty) {
      this.failOnEmpty = failOnEmpty;
   }
}