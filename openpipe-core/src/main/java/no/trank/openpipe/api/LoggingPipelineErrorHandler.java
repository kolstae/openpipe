package no.trank.openpipe.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.Document;

/**
 * A simple error-handler that logs all exceptions.
 *
 * @version $Revision$
 */
public class LoggingPipelineErrorHandler implements PipelineErrorHandler {
   private static final Logger log = LoggerFactory.getLogger(LoggingPipelineErrorHandler.class);

   @Override
   public void handleException(boolean finish, PipelineException ex) {
      if (finish) {
         log.error("Finish failed for '" + ex.getPipelineStepName() + "'", ex);
      } else {
         log.error("Prepare failed for '" + ex.getPipelineStepName() + "'", ex);
      }
   }

   @Override
   public void handleException(Document document, PipelineException ex) {
      log.error("Exception thrown in '" + ex.getPipelineStepName() + "'", ex);
      if (log.isDebugEnabled()) {
         log.error(document.toString());
      }
   }
}
