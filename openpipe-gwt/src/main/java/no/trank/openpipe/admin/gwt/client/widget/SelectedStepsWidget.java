package no.trank.openpipe.admin.gwt.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import no.trank.openpipe.admin.gwt.client.IStepServer;
import no.trank.openpipe.admin.gwt.client.IStepServerAsync;
import no.trank.openpipe.admin.gwt.client.model.StepConfig;
import no.trank.openpipe.admin.gwt.client.model.StepValues;

/**
 * @version $Revision$
 */
public class SelectedStepsWidget extends Composite {
   private final IStepServerAsync stepServer = IStepServer.Instance.getInstance();
   private final VerticalPanel top = new VerticalPanel();
   private final EditStepWidget editStepWidget;
   private final SubpipelinesWidget subpipelinesWidget = new SubpipelinesWidget();
   
   private List steps = new ArrayList();
   
   public SelectedStepsWidget(EditStepWidget editStepWidget) {
      this.editStepWidget = editStepWidget;
      initWidget(top);
      loadSteps();
   }
   
   private void loadSteps() {
      top.clear();
      for(int i = 0; i < steps.size(); ++i) {
         final StepValues step = (StepValues)steps.get(i);
         Hyperlink lab = new Hyperlink();
         lab.setText((String)step.getValue("name"));
         lab.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
               stepClicked(step);
            }
         });
         top.add(lab);
      }
      
      subpipelinesWidget.update(steps);
      
      top.add(new Label("SUBPIPLINES"));
      top.add(subpipelinesWidget);
   }

   public void stepClicked(StepValues stepValues) {
      editStepWidget.setEditStep(stepValues);
   }

   public void addStep(StepConfig stepConfig) {
      StepValues stepValues = new StepValues();
      stepValues.setStepConfig(stepConfig);
      steps.add(stepValues);
      loadSteps();
      editStepWidget.setEditStep(stepValues);
   }
}
