package no.trank.openpipe.admin.gwt.client.model.type;

/**
 * @version $Revision: 874 $
 */
public class ErrorType implements FieldType {
   private String error;
   
   public String getTypeName() {
      return "error";
   }
   
   public FieldType[] getTypes() {
      return empty;
   }
   
   public String toString() {
      return "ERROR: " + error;
   }
   
   public Object getDefault() {
      return "ERROR";
   }
   
   public boolean isEmpty(Object value) {
      return false;
   }
   
   public Object clean(Object value) {
      return null;
   }
   
   public static FieldType getInstance(String error) {
      ErrorType ret = new ErrorType();
      ret.error = error;
      return ret;
   }
}