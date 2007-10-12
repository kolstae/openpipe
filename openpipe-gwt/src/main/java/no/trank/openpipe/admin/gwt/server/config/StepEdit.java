package no.trank.openpipe.admin.gwt.server.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import no.trank.openpipe.api.PipelineStep;

/**
 * @version $Revision: 874 $
 */
public class StepEdit {
   private PipelineStep step; 
   private Map<String, Type> fields = new LinkedHashMap<String, Type>();
   private Map<String, Object> values = new HashMap<String, Object>();
   
   public PipelineStep getStep() {
      return step;
   }
   public void setStep(PipelineStep step) {
      this.step = step;
   }
   public Map<String, Type> getFields() {
      return fields;
   }
   public void setFields(Map<String, Type> fields) {
      this.fields = fields;
   }
   public Map<String, Object> getValues() {
      return values;
   }
   public void setValues(Map<String, Object> values) {
      this.values = values;
   }
}