package no.trank.openpipe.admin.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

import no.trank.openpipe.admin.gwt.client.widget.AvailableStepsWidget;
import no.trank.openpipe.admin.gwt.client.widget.EditStepWidget;
import no.trank.openpipe.admin.gwt.client.widget.SelectedStepsWidget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @version $Revision: 874 $
 */
public class Main implements EntryPoint {
   
   public Main() {
   }
   
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
     HorizontalPanel panel = new HorizontalPanel();
     EditStepWidget editStepWidget = new EditStepWidget(); 
     SelectedStepsWidget sw = new SelectedStepsWidget(editStepWidget);
     panel.add(new AvailableStepsWidget(sw));
     panel.add(sw);
     panel.add(editStepWidget);
     RootPanel.get().add(panel);
  }
}
