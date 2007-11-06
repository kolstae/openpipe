package no.trank.openpipe.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.Document;

/**
 * A simple error-handler that logs all exceptions.
 *
 * @version $Revision$
 */
public class LoggingPipelineExceptionListener implements PipelineExceptionListener {
   private static final Logger log = LoggerFactory.getLogger(LoggingPipelineExceptionListener.class);

   public void onException(PipelineException ex, Document document) {
      log.error("Exception thrown in '" + ex.getPipelineStepName() + "'", ex);
      if (document != null) {
         if (log.isDebugEnabled()) {
            log.error(document.toString());
         }
      }
   }
}
