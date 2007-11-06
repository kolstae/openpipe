package no.trank.openpipe.api;

import java.util.Set;

import no.trank.openpipe.util.IdentityHashSet;
import no.trank.openpipe.api.document.Document;

/**
 * A base implementation for the PipelineExceptionHandler to simplify handling of
 * ExceptionListeners.
 *
 * @version $Revision$
 */
public abstract class BasePipelineExceptionHandler implements PipelineExceptionHandler {
   protected IdentityHashSet<PipelineExceptionListener> exceptionListeners = new IdentityHashSet<PipelineExceptionListener>();

   public void addExceptionListener(PipelineExceptionListener exceptionListener) {
      exceptionListeners.add(exceptionListener);
   }

   public void removeExceptionListener(PipelineExceptionListener exceptionListener) {
      exceptionListeners.remove(exceptionListener);
   }

   public Set<PipelineExceptionListener> getExceptionListeners() {
      return exceptionListeners;
   }

   public void setExceptionListeners(Set<PipelineExceptionListener> exceptionListeners) {
      this.exceptionListeners = new IdentityHashSet<PipelineExceptionListener>(exceptionListeners);
   }

   /**
    * This method should be called on all exceptions.
    *
    * @param ex the exception that was handled
    */
   protected void notifyExceptionListeners(PipelineException ex) {
      notifyExceptionListeners(ex, null);
   }

   /**
    * This method should be called on all exceptions.
    *
    * @param ex the exception that was handled
    * @param document the document(if any) this exception was thrown on
    */
   protected void notifyExceptionListeners(PipelineException ex, Document document) {
      for (PipelineExceptionListener exceptionListener : exceptionListeners) {
         exceptionListener.onException(ex, document);
      }
   }

}
