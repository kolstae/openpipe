package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class RegexField extends BasePipelineStep {
   private static Logger log = LoggerFactory.getLogger(RegexField.class);
   
   private Map<String, String> fieldNameMap;
   private Pattern fromPattern;
   private String toPattern;
   private boolean copyOnMiss;

   public RegexField() {
      super("RegexField");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      if (fieldNameMap != null) {
         for(Map.Entry<String, String> pair : fieldNameMap.entrySet()) {
            process(doc, pair.getKey(), pair.getValue());
         }
      }

      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   public PipelineStepStatus process(Document doc, String fromFieldName, String toFieldName) throws PipelineException {
      List<String> values = doc.getFieldValues(fromFieldName);
      List<String> outValues = new ArrayList<String>();
      
      if (values == null || values.isEmpty()) {
         log.debug("Missing field '{}'", fromFieldName);
      } else {
         for (String value : values) {
            Matcher m = fromPattern.matcher(value);
            if (m.matches()) {
               log.debug("Field '{}' matches", fromFieldName);
               outValues.add(m.replaceAll(toPattern));
            } else {
               log.debug("Field '{}' does not match", fromFieldName);
               if (copyOnMiss) {
                  outValues.add(value);
               }
            }
         }
      }
      
      if (outValues.isEmpty()) {
         doc.removeField(toFieldName);
      } else {
         doc.setFieldValues(toFieldName, outValues);
      }

      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public String getFromPattern() {
      return fromPattern != null ? fromPattern.pattern() : null;
   }

   public void setFromPattern(String fromPattern) {
      this.fromPattern = Pattern.compile(fromPattern);
   }

   public String getToPattern() {
      return toPattern;
   }

   public void setToPattern(String toPattern) {
      this.toPattern = toPattern;
   }

   public boolean isCopyOnMiss() {
      return copyOnMiss;
   }

   public void setCopyOnMiss(boolean copyOnMiss) {
      this.copyOnMiss = copyOnMiss;
   }

   public Map<String, String> getFieldNameMap() {
      return fieldNameMap;
   }

   public void setFieldNameMap(Map<String, String> fieldNameMap) {
      this.fieldNameMap = fieldNameMap;
   }
}