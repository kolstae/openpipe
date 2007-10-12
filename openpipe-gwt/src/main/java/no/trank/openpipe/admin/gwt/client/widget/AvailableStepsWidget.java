package no.trank.openpipe.admin.gwt.client.widget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import no.trank.openpipe.admin.gwt.client.IStepServer;
import no.trank.openpipe.admin.gwt.client.IStepServerAsync;
import no.trank.openpipe.admin.gwt.client.model.StepConfig;
import no.trank.openpipe.admin.gwt.client.util.StepUtil;

/**
 * @version $Revision: 874 $
 */
public class AvailableStepsWidget extends Composite {
   private final IStepServerAsync stepServer = IStepServer.Instance.getInstance();
   private final VerticalPanel top = new VerticalPanel();
   private final SelectedStepsWidget selectedStepsWidget;
   
   public AvailableStepsWidget(SelectedStepsWidget selectedStepsWidget) {
      this.selectedStepsWidget = selectedStepsWidget;
      initWidget(top);
      loadSteps();
   }
   
   private void loadSteps() {
      stepServer.getAvailableSteps(new AsyncCallback() {
         public void onSuccess(Object result) {
            StepConfig[] steps = (StepConfig[])result;
            top.clear();
            for(int i = 0; i < steps.length; ++i) {
               final StepConfig step = steps[i];
               Hyperlink lab = new Hyperlink();
               lab.setText(StepUtil.getName(step));
               lab.addClickListener(new ClickListener() {
                  public void onClick(Widget sender) {
                     stepClicked(step);
                  }
               });
               top.add(lab);
            }
         }

         public void onFailure(Throwable caught) {
            top.clear();
            top.add(new Label("ERROR: " + caught));
         }
      });
   }

   public void stepClicked(StepConfig stepConfig) {
      selectedStepsWidget.addStep(stepConfig);
   }
}
