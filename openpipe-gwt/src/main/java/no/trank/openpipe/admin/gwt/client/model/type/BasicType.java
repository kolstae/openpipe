package no.trank.openpipe.admin.gwt.client.model.type;

/**
 * @version $Revision: 874 $
 */
public class BasicType implements FieldType {
   private String primitiveName;
   
   public String getTypeName() {
      return primitiveName;
   }
   
   public FieldType[] getTypes() {
      return empty;
   }
   
   public String toString() {
      return getTypeName().toUpperCase();
   }
   
   public Object getDefault() {
      if("byte".equals(primitiveName)) return new Byte((byte)0);
      if("short".equals(primitiveName)) return new Short((short)0);
      if("char".equals(primitiveName)) return new Character('\0');
      if("int".equals(primitiveName)) return new Integer(0);
      if("long".equals(primitiveName)) return new Long(0L);
      if("float".equals(primitiveName)) return new Float(0.0F);
      if("double".equals(primitiveName)) return new Double(0.0);
      return null;
   }
   
   public Object getValueFromString(String text) {
      text = text != null ? text.trim() : "";
      text = text.replace(',', '.');
      if(text.length() > 0) {
         try {
            if("byte".equals(primitiveName)) return new Byte(text);
            if("short".equals(primitiveName)) return new Short(text);
            if("char".equals(primitiveName)) return new Character(text.charAt(0));
            if("int".equals(primitiveName)) return new Integer(text);
            if("long".equals(primitiveName)) return new Long(text);
            if("float".equals(primitiveName)) return new Float(text);
            if("double".equals(primitiveName)) return new Double(text);
         }
         catch(Exception e) {
         }
      }
      return null;
   }
   
   public boolean isEmpty(Object value) {
      return value == null ? true : value.equals(getDefault());
   }
   
   public Object clean(Object value) {
      return isEmpty(value) ? null : value;
   }
   
   public static FieldType getInstance(String primitiveName) {
      BasicType ret = new BasicType();
      ret.primitiveName = primitiveName;
      return ret;
   }
}