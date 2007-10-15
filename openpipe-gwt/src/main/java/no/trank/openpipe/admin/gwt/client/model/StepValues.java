package no.trank.openpipe.admin.gwt.client.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @version $Revision$
 */
public class StepValues implements IsSerializable {
   private StepConfig stepConfig;
   private Map values = new HashMap();

   public StepConfig getStepConfig() {
      return stepConfig;
   }

   public void setStepConfig(StepConfig stepConfig) {
      this.stepConfig = stepConfig;
   }

   public Map getValues() {
      return values;
   }
   
   public Object getValue(String key) {
      Object ret = values.get(key);
      return ret != null ? ret : stepConfig.getValues().get(key);
   }

   public boolean setValue(String key, Object value) {
      if(value == null) {
         values.remove(key);
      }
      else {
         values.put(key, value);
      }
      // TODO: remove if default. return false if no change
      return true;
   }
}
