package no.trank.openpipe.admin.gwt.client.model.type;

import no.trank.openpipe.admin.gwt.client.model.StepConfig;

/**
 * @version $Revision: 874 $
 */
public class StepType implements FieldType {
   public String getTypeName() {
      return "step";
   }
   
   public FieldType[] getTypes() {
      return empty;
   }
   
   public String toString() {
      return "STEP";
   }
   
   public Object getDefault() {
      return new StepConfig();
   }
   
   public boolean isEmpty(Object value) {
      String className = value != null ? ((StepConfig)value).getClassName() : null;
      return className != null && className.length() > 0;
   }
   
   public Object clean(Object value) {
      return isEmpty(value) ? null : value;
   }

   // TODO: consider making one instance per class
   public static FieldType getInstance() {
      return new StepType();
   }
}