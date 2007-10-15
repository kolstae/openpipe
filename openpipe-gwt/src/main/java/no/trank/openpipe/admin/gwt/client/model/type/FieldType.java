package no.trank.openpipe.admin.gwt.client.model.type;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @version $Revision$
 */
public interface FieldType extends IsSerializable {
   String getTypeName();
   FieldType[] getTypes();
   Object getDefault();
   boolean isEmpty(Object value);
   Object clean(Object value);
   
   static FieldType[] empty = new FieldType[0]; 
}