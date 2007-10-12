package no.trank.openpipe.admin.gwt.client.model.type;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import no.trank.openpipe.admin.gwt.client.util.Linked14Map;

/**
 * @version $Revision: 874 $
 */
public class MapType implements FieldType {
   private FieldType[] fieldTypes = new FieldType[2];
   
   public String getTypeName() {
      return "map";
   }
   
   public FieldType[] getTypes() {
      return fieldTypes;
   }
   
   public String toString() {
      return "MAP<" + fieldTypes[0] + ", " + fieldTypes[1] + ">";
   }
   
   public Object getDefault() {
      return new Linked14Map();
   }
   
   public boolean isEmpty(Object value) {
      if(value instanceof Map) {
         for(Iterator it = ((Map)value).entrySet().iterator(); it.hasNext(); ) {
            if(!fieldTypes[0].isEmpty(((Entry)it.next()).getKey())) {
               return false;
            }
         }
      }
      return true;
   }
   
   public Object clean(Object value) {
      Map ret = (Map)getDefault();
      if(value instanceof Map) {
         ret.putAll((Map)value);
      }
      for(Iterator it = ret.entrySet().iterator(); it.hasNext(); ) {
         if(fieldTypes[0].isEmpty(((Entry)it.next()).getKey())) {
            it.remove();
         }
      }
      return ret;
   }
   
   public static FieldType getInstance(FieldType keyType, FieldType valueType) {
      MapType ret = new MapType();
      ret.fieldTypes[0] = keyType;
      ret.fieldTypes[1] = valueType;
      return ret;
   }
}