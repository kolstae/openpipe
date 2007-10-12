package no.trank.openpipe.admin.gwt.client.widget;

import java.util.List;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import no.trank.openpipe.admin.gwt.client.model.Subpipeline;
import no.trank.openpipe.admin.gwt.client.util.StepUtil;

/**
 * @version $Revision: 874 $
 */
public class SubpipelinesWidget extends Composite {
   private final VerticalPanel top = new VerticalPanel();
   
   private Subpipeline[] subpipelines;
   
   public SubpipelinesWidget() {
      initWidget(top);
   }
   
   public void update(List steps) {
      top.clear();
      subpipelines = StepUtil.getSubpipelines(steps);
      for(int i = 0; i < subpipelines.length; ++i) {
         Hyperlink lab = new Hyperlink();
         lab.setText("SUBPIPELINE");
         lab.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
               //stepClicked(step);
            }
         });
         top.add(lab);
      }
   }
}
