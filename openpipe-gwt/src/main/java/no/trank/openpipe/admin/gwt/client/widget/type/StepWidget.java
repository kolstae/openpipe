package no.trank.openpipe.admin.gwt.client.widget.type;

import com.google.gwt.user.client.ui.Label;

import no.trank.openpipe.admin.gwt.client.model.StepConfig;
import no.trank.openpipe.admin.gwt.client.model.type.StepType;
import no.trank.openpipe.admin.gwt.client.model.value.ValueWrapper;

/**
 * @version $Revision$
 */
public class StepWidget extends Label {
   public StepWidget(final StepType type, final ValueWrapper wrapper) {
      super();
      
      StepConfig value = (StepConfig)wrapper.getValue();
      String className = value != null ? value.getClassName() : null;
      setText(className != null && className.length() > 0 ? className : "[NONE]");

//      addChangeListener(new ChangeListener() {
//         public void onChange(Widget sender) {
//            String text = getText();
//            String tmp = (String)wrapper.setValue(getText());
//            if(!tmp.equals(text)) {
//               setText(tmp);
//            }
//         }
//      });
   }
}