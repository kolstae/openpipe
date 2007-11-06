package no.trank.openpipe.step;

import java.util.Map;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class SetField extends BasePipelineStep {
   private Map<String, String> fieldValueMap;
   private boolean overwrite = true;

   public SetField() {
      super("SetField");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      for (Map.Entry<String, String> entry : fieldValueMap.entrySet()) {
         if (overwrite || !doc.containsField(entry.getKey())) {
            doc.setFieldValue(entry.getKey(), entry.getValue());
         }
      }
      return PipelineStepStatus.DEFAULT;
   }

   public Map<String, String> getFieldValueMap() {
      return fieldValueMap;
   }

   public void setFieldValueMap(Map<String, String> fieldValueMap) {
      this.fieldValueMap = fieldValueMap;
   }

   public boolean isOverwrite() {
      return overwrite;
   }

   public void setOverwrite(boolean overwrite) {
      this.overwrite = overwrite;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }
}
