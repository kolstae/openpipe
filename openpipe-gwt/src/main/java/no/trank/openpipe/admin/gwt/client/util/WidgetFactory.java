package no.trank.openpipe.admin.gwt.client.util;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import no.trank.openpipe.admin.gwt.client.model.type.BasicType;
import no.trank.openpipe.admin.gwt.client.model.type.FieldType;
import no.trank.openpipe.admin.gwt.client.model.type.ListType;
import no.trank.openpipe.admin.gwt.client.model.type.MapType;
import no.trank.openpipe.admin.gwt.client.model.type.StepType;
import no.trank.openpipe.admin.gwt.client.model.type.StringType;
import no.trank.openpipe.admin.gwt.client.model.value.ValueWrapper;
import no.trank.openpipe.admin.gwt.client.widget.type.BooleanWidget;
import no.trank.openpipe.admin.gwt.client.widget.type.ListWidget;
import no.trank.openpipe.admin.gwt.client.widget.type.MapWidget;
import no.trank.openpipe.admin.gwt.client.widget.type.NumericWidget;
import no.trank.openpipe.admin.gwt.client.widget.type.StepWidget;
import no.trank.openpipe.admin.gwt.client.widget.type.StringWidget;

/**
 * @version $Revision: 874 $
 */
public class WidgetFactory {
   public static Widget getWidget(FieldType type, final ValueWrapper wrapper) {
      if(type instanceof ListType) {
         return new ListWidget((ListType)type, wrapper);   
      }
      if(type instanceof MapType) {
         return new MapWidget((MapType)type, wrapper);
      }
      if(type instanceof StringType) {
         return new StringWidget((StringType)type, wrapper);
      }
      if(type instanceof StepType) {
         return new StepWidget((StepType)type, wrapper);
      }
      if("boolean".equals(type.getTypeName())) {
         return new BooleanWidget(type, wrapper);
      }
      if(type instanceof BasicType) {
         return new NumericWidget((BasicType)type, wrapper);
      }
      return new Label(type.toString());
   }
}
