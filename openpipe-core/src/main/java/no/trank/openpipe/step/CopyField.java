package no.trank.openpipe.step;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class CopyField extends BasePipelineStep {
   private String fromFieldName;
   private String toFieldName;
   private boolean withAnnotations;
   private boolean overwrite = true;

   public CopyField() {
      super("CopyField");
   }

   @Override
   public PipelineStepStatus execute(Document doc) {
      if (doc.containsField(fromFieldName) && (overwrite || !doc.containsField(toFieldName))) {
         if (withAnnotations) {
            doc.setField(toFieldName, doc.getFields(fromFieldName));
         } else {
            doc.setFieldValues(toFieldName, doc.getFieldValues(fromFieldName));
         }
      }

      return PipelineStepStatus.DEFAULT;
   }

   @Override
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

   public boolean isWithAnnotations() {
      return withAnnotations;
   }

   public void setWithAnnotations(boolean withAnnotations) {
      this.withAnnotations = withAnnotations;
   }

   public boolean isOverwrite() {
      return overwrite;
   }

   public void setOverwrite(boolean overwrite) {
      this.overwrite = overwrite;
   }
}
