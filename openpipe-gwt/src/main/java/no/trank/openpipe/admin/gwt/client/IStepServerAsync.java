package no.trank.openpipe.admin.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @version $Revision: 874 $
 */
public interface IStepServerAsync extends RemoteService {
   void getAvailableSteps(AsyncCallback callback);
}