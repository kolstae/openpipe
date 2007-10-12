package no.trank.openpipe.admin.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import no.trank.openpipe.admin.gwt.client.model.StepConfig;

/**
 * @version $Revision: 874 $
 */
public interface IStepServer extends RemoteService {
   StepConfig[] getAvailableSteps();
   
   public static class Instance {
      private static IStepServerAsync ourInstance = (IStepServerAsync)GWT.create(IStepServer.class);
      static {
         ((ServiceDefTarget)ourInstance).setServiceEntryPoint(GWT.getModuleBaseURL() + "step");
      }

      public static IStepServerAsync getInstance() {
         return ourInstance;
      }
   }
}