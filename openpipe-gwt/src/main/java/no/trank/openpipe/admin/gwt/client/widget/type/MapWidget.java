package no.trank.openpipe.admin.gwt.client.widget.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import no.trank.openpipe.admin.gwt.client.model.type.MapType;
import no.trank.openpipe.admin.gwt.client.model.value.ValueWrapper;
import no.trank.openpipe.admin.gwt.client.util.WidgetFactory;

/**
 * @version $Revision: 874 $
 */
public class MapWidget extends Composite {
   private final VerticalPanel top = new VerticalPanel();
   private final MapType type;
   private final Map values;
   private List keys;
   private List panels;
   
   public MapWidget(MapType type, ValueWrapper wrapper) {
      this.type = type;

      Map tmp = (Map)wrapper.getValue();
      if(tmp != null) {
         values = tmp;
      }
      else {
         values = (Map)type.getDefault();
         wrapper.setValue(values);
      }

      initWidget(top);
      
      reset();
   }
   
   private void reset() {
      top.clear();
      keys = new ArrayList(values.keySet());
      panels = new ArrayList(keys.size());
      
      for(int i = 0; i < keys.size(); ++i) {
         Panel row = getRow(i);
         top.add(row);
         panels.add(row);
      }

      update();
   }
   
   private void update() {
      if(getEmptyCount() == 0) {
         Object key = type.getTypes()[0].getDefault();
         Object value = type.getTypes()[1].getDefault();
         keys.add(key);
         values.put(key, value);
         Panel row = getRow(keys.size() - 1);
         top.add(row);
         panels.add(row);
      }
   }
   
   private void removeEmpty() {
      for(int index = keys.size() - 1; index >= 0; --index) {
         Panel row = (Panel)panels.get(index);
         Object key = keys.get(index);
         if(row != null && type.getTypes()[0].isEmpty(key)) {
            top.remove(row);
            panels.set(index, null);
            keys.set(index, null);
            values.remove(key);
         }
      }
   }
   
   private int getEmptyCount() {
      int empty = 0;
      
      for(int i = 0; i < keys.size(); ++i) {
         if(panels.get(i) != null && type.getTypes()[0].isEmpty(keys.get(i))) {
            ++empty;
         }
      }
      return empty;
   }
   
   private Panel getRow(final int index) {
      HorizontalPanel ret = new HorizontalPanel();
      
      ValueWrapper keyWrapper = new ValueWrapper() {
         public Object getValue() {
            return keys.get(index);
         }

         public Object setValue(Object key) {
            Object oldKey = keys.get(index);
            
            if(oldKey.equals(key)) {
               return oldKey;
            }
            
            boolean oldEmpty = type.getTypes()[0].isEmpty(oldKey);
            boolean newEmpty = type.getTypes()[0].isEmpty(key);
            
            if(newEmpty && !oldEmpty) {
               removeEmpty();
            }
            
            if(values.get(key) != null) { // key exists
               return oldKey;
            }
            
            Object tmp = values.get(oldKey);
            values.remove(oldKey);
            values.put(key, tmp);
            keys.set(index, key);

            update();
            
            return key;
         }
      };
      
      ValueWrapper valueWrapper = new ValueWrapper() {
         public Object getValue() {
            return values.get(keys.get(index));
         }

         public Object setValue(Object value) {
            values.put(keys.get(index), value);
            
            update();
            
            return value;
         }
      };
      
      ret.add(WidgetFactory.getWidget(type.getTypes()[0], keyWrapper));
      ret.add(WidgetFactory.getWidget(type.getTypes()[1], valueWrapper));
      return ret;
   }
}