package no.trank.openpipe.step;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision: 874 $
 */
public class RemoveFields extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(RemoveFields.class);
   private List<String> fieldNames;

   public RemoveFields() {
      super("RemoveField");
   }

   public PipelineStepStatus execute(Document doc) {
      if(fieldNames != null) {
         for(String fieldName : fieldNames) {
            if(doc.getFieldValue(fieldName) != null) {
               log.debug("Removing field '{}'", fieldName);
               doc.removeField(fieldName);
            }
         }
      }

      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   public String getRevision() {
      return "$Revision: 874 $";
   }

   public List<String> getFieldNames() {
      return fieldNames;
   }

   public void setFieldNames(List<String> fieldNames) {
      this.fieldNames = fieldNames;
   }
}