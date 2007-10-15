package no.trank.openpipe.admin.gwt.client.widget.type;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

import no.trank.openpipe.admin.gwt.client.model.type.FieldType;
import no.trank.openpipe.admin.gwt.client.model.value.ValueWrapper;

/**
 * @version $Revision$
 */
public class BooleanWidget extends CheckBox {
   public BooleanWidget(final FieldType type, final ValueWrapper wrapper) {
      super();
      
      Object value = wrapper.getValue();
      setChecked(value != null && ((Boolean)value).booleanValue());
      addClickListener(new ClickListener() {
         public void onClick(Widget sender) {
            Object o1 = ((CheckBox)sender).isChecked() ? Boolean.TRUE : Boolean.FALSE;
            Object o2 = wrapper.setValue(o1);
            if(o1 != o2) { // it is theoretically possible, if the key type in a map is Boolean
               setChecked(((Boolean)o2).booleanValue());
            }
         }
      });
   }
}