package no.trank.openpipe.admin.gwt.client.widget.type;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import no.trank.openpipe.admin.gwt.client.model.type.FieldType;
import no.trank.openpipe.admin.gwt.client.model.value.ValueWrapper;

/**
 * @version $Revision$
 */
public class StringWidget extends TextBox {
   public StringWidget(final FieldType type, final ValueWrapper wrapper) {
      super();
      
      Object value = wrapper.getValue();
      setText(value != null ? (String)value : "");
      addChangeListener(new ChangeListener() {
         public void onChange(Widget sender) {
            String text = getText();
            String tmp = (String)wrapper.setValue(getText());
            if(!tmp.equals(text)) {
               setText(tmp);
            }
         }
      });
   }
}