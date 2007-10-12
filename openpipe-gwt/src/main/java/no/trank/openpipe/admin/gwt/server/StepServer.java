package no.trank.openpipe.admin.gwt.server;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import no.trank.openpipe.admin.gwt.client.IStepServer;
import no.trank.openpipe.admin.gwt.client.model.StepConfig;
import no.trank.openpipe.admin.gwt.client.util.Linked14Map;
import no.trank.openpipe.admin.gwt.server.biz.StepManager;
import no.trank.openpipe.admin.gwt.server.biz.TypeManager;
import no.trank.openpipe.admin.gwt.server.config.StepEdit;

/**
 * @version $Revision: 874 $
 */
public class StepServer extends RemoteServiceServlet implements IStepServer {
   private static final long serialVersionUID = -3522045718383373709L;
   private static StepManager stepManager = new StepManager();
   private static TypeManager typeManager = new TypeManager();
   static {
      stepManager.setConfigFile("pipelineConfigContext.xml");
      stepManager.init();
      typeManager.setStepManager(stepManager);
   }

   public StepConfig[] getAvailableSteps() {
      List<StepEdit> steps = stepManager.getDefaultSteps();
      List<StepConfig> ret = new ArrayList<StepConfig>();
      for(StepEdit e : steps) {
         StepConfig conf = new StepConfig();
         conf.setClassName(e.getStep().getClass().getName());
         Map fieldMap = new Linked14Map();
         for(Entry<String, Type> entry : e.getFields().entrySet()) {
            fieldMap.put(entry.getKey(), TypeManager.getFieldType(entry.getValue()));
         }
         conf.setFieldMap(fieldMap);
         conf.setValues(e.getValues());
         ret.add(conf);
      }
      
      return ret.toArray(new StepConfig[ret.size()]);
   }
}
