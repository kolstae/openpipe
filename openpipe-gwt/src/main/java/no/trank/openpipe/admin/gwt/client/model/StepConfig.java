package no.trank.openpipe.admin.gwt.client.model;

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @version $Revision: 874 $
 */
public class StepConfig implements IsSerializable {
   private String className;
   
   /**
    * @gwt.typeArgs <java.lang.String, no.trank.pipeline.admin.gwt.client.model.FieldType>
    */
   private Map fieldMap;

   private Map values;

   public String getClassName() {
      return className;
   }

   public void setClassName(String className) {
      this.className = className;
   }

   public Map getFieldMap() {
      return fieldMap;
   }

   public void setFieldMap(Map fieldMap) {
      this.fieldMap = fieldMap;
   }

   public Map getValues() {
      return values;
   }

   public void setValues(Map values) {
      this.values = values;
   }
}
