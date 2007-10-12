package no.trank.openpipe.admin.gwt.client.model.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @version $Revision: 874 $
 */
public class ListType implements FieldType {
   private FieldType[] listType = new FieldType[1];
   private String type;
   
   public String getTypeName() {
      return type;
   }
   
   public FieldType[] getTypes() {
      return listType;
   }
   
   public String toString() {
      return type.toUpperCase() + "<" + listType[0] + ">";
   }
   
   public Object getDefault() {
      return new ArrayList();
   }

   public boolean isEmpty(Object value) {
      if(value instanceof Collection) {
         for(Iterator it = ((Collection)value).iterator(); it.hasNext(); ) {
            if(!listType[0].isEmpty(it.next())) {
               return false;
            }
         }
      }
      return true;
   }
   
   public Object clean(Object value) {
      List ret = (List)getDefault();
      if(value instanceof List) {
         for(Iterator it = ((List)value).iterator(); it.hasNext(); ) {
            Object ob = it.next();
            if(!listType[0].isEmpty(ob)) {
               ret.add(ob);
            }
         }
      }
      return ret;
   }
   
   public static FieldType getInstance(FieldType listType, String type) {
      ListType ret = new ListType();
      ret.listType[0] = listType;
      ret.type = type;
      return ret;
   }
}