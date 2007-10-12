package no.trank.openpipe.admin.gwt.client.widget.type;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import no.trank.openpipe.admin.gwt.client.model.type.BasicType;
import no.trank.openpipe.admin.gwt.client.model.value.ValueWrapper;

/**
 * @version $Revision: 874 $
 */
public class NumericWidget extends TextBox {
   public NumericWidget(final BasicType type, final ValueWrapper wrapper) {
      super();
      
      setValue(wrapper.getValue());
      addChangeListener(new ChangeListener() {
         public void onChange(Widget sender) {
            Object newValue = type.getValueFromString(getText());
            if(newValue == null) {
               if(getText().trim().length() > 0) {
                  setValue(wrapper.getValue());
               }
            }
            else {
               Object realValue = wrapper.setValue(newValue);
               if(!newValue.equals(realValue)) {
                  setValue(realValue);
               }
            }
         }
      });
   }
   
   private void setValue(Object value) {
      setText(value != null ? value.toString() : "");
   }
}