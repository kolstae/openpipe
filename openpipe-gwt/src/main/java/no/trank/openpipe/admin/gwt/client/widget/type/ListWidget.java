package no.trank.openpipe.admin.gwt.client.widget.type;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import no.trank.openpipe.admin.gwt.client.model.type.FieldType;
import no.trank.openpipe.admin.gwt.client.model.type.ListType;
import no.trank.openpipe.admin.gwt.client.model.value.ValueWrapper;
import no.trank.openpipe.admin.gwt.client.util.WidgetFactory;

/**
 * @version $Revision$
 */
public class ListWidget extends Composite {
   private final VerticalPanel top = new VerticalPanel();
   private final ListType type;
   private final List values;
   
   public ListWidget(ListType type, ValueWrapper wrapper) {
      this.type = type;
      
      List tmp = (List)wrapper.getValue();
      values = tmp != null ? tmp : (List)wrapper.setValue(type.getDefault());
      
      for(int i = 0; i < values.size(); ++i) {
         top.add(getRow(i));
      }
      
      initWidget(top);
      
      update();
   }

   private void update() {
      int empty = 0;
      
      FieldType elementType = type.getTypes()[0];
      for(int i = 0; i < values.size(); ++i) {
         if(elementType.isEmpty(values.get(i))) {
            ++empty;
         }
      }
      
      if(empty == 0) {
         values.add(elementType.getDefault());
         top.add(getRow(values.size() - 1));
      }
   }
   
   private Widget getRow(final int index) {
      return WidgetFactory.getWidget(type.getTypes()[0], new ValueWrapper() {
         public Object getValue() {
            return values.get(index);
         }

         public Object setValue(Object value) {
            values.set(index, value);
            update();
            return value;
         }
      });
   }
}