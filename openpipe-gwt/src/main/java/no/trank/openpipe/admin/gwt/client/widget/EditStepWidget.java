package no.trank.openpipe.admin.gwt.client.widget;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import no.trank.openpipe.admin.gwt.client.model.StepValues;
import no.trank.openpipe.admin.gwt.client.model.type.FieldType;
import no.trank.openpipe.admin.gwt.client.model.value.ValueWrapper;
import no.trank.openpipe.admin.gwt.client.util.WidgetFactory;

/**
 * @version $Revision: 874 $
 */
public class EditStepWidget extends Composite {
   private final VerticalPanel top = new VerticalPanel();
   private StepValues step;
   
   public EditStepWidget() {
      initWidget(top);
      renderStep();
   }
   
   private void renderStep() {
      top.clear();
      if(step != null) {
         FlexTable table = new FlexTable();
         table.setBorderWidth(0);
         table.setCellPadding(0);
         table.setCellSpacing(0);
         
         table.setWidget(0, 0, new Label(step.getStepConfig().getClassName()));
         for(Iterator it = step.getStepConfig().getFieldMap().entrySet().iterator(); it.hasNext(); ) {
            Entry entry = (Entry)it.next();
            final String key = (String)entry.getKey();
            int row = table.getRowCount();
            table.setWidget(row, 0, new Label(key));

            ValueWrapper wrapper = new ValueWrapper() {
               public Object getValue() {
                  return step.getValue(key);
               }

               public Object setValue(Object value) {
                  step.setValue(key, value);
                  return step.getValue(key);
               }
            };
            
            table.setWidget(row, 1, WidgetFactory.getWidget((FieldType)entry.getValue(), wrapper));
            
            table.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
            table.getCellFormatter().setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);
         }
         
         top.add(table);
      }
   }
   
   public void setEditStep(StepValues stepValues) {
      this.step = stepValues;
      renderStep();
   }
}