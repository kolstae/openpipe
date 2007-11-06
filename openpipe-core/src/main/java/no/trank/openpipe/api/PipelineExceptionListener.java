package no.trank.openpipe.api;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public interface PipelineExceptionListener {
   void onException(PipelineException ex, Document document);
}
