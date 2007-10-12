package no.trank.openpipe.admin.gwt.client.model.type;

/**
 * @version $Revision: 874 $
 */
public class StringType implements FieldType {
   private static FieldType instance = new StringType();
   
   public String getTypeName() {
      return "string";
   }
   
   public FieldType[] getTypes() {
      return empty;
   }
   
   public String toString() {
      return "STRING";
   }

   public Object getDefault() {
      return "";
   }
   
   public boolean isEmpty(Object value) {
      return value == null || ((String)value).trim().length() == 0;
   }
   
   public Object clean(Object value) {
      return isEmpty(value) ? null : value;
   }
   
   public static FieldType getInstance() {
      return instance;
   }
}