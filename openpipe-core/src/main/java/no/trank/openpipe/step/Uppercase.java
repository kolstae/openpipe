package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision: 874 $
 */
public class Uppercase extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(Uppercase.class);
   private Map<String, String> fieldNameMap;

   public Uppercase() {
      super("Uppercase");
   }

   public PipelineStepStatus execute(Document doc) {
      if (fieldNameMap != null) {
         for(Map.Entry<String, String> pair : fieldNameMap.entrySet()) {
            process(doc, pair.getKey(), pair.getValue());
         }
      }

      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   private static void process(Document doc, String input, String output) {
      if (!doc.containsField(input)) {
         log.debug("Field '{}' - null; Output field: '{}'", input, output);
      } else {
         final List<String> values = doc.getFieldValues(input);
         final ArrayList<String> newValues = new ArrayList<String>(values.size());
         for (String text : values) {
            log.debug("Field '{}' length: {}; Output field: '{}'", new Object[] { input, text.length(), output } );
            newValues.add(text.toUpperCase());
         }
         doc.setFieldValues(output, newValues);
      }
   }

   public String getRevision() {
      return "$Revision: 874 $";
   }

   public void setFieldNameMap(Map<String, String> fieldNameMap) {
      this.fieldNameMap = fieldNameMap;
   }

   public Map<String, String> getFieldNameMap() {
      return fieldNameMap;
   }
}