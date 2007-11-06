package no.trank.openpipe.api;

import no.trank.openpipe.api.document.Document;

/**
 * The default exception handler. This will return a <tt>PipelineFlowEnum.STOP</tt> for all
 * exceptions. 
 *
 * @version $Revision$
 *
 * @see no.trank.openpipe.api.PipelineFlow
 * @see no.trank.openpipe.api.PipelineFlowEnum
 */
public class DefaultPipelineExceptionHandler extends BasePipelineExceptionHandler {

   public PipelineFlow handlePrepareException(PipelineException ex) {
      notifyExceptionListeners(ex);
      return PipelineFlowEnum.STOP;
   }

   public void handleFinishException(PipelineException ex) {
      notifyExceptionListeners(ex);
   }

   public PipelineFlow handleProducerException(PipelineException ex) {
      notifyExceptionListeners(ex);
      return PipelineFlowEnum.STOP;
   }

   public PipelineFlow handleDocumentException(PipelineException ex, Document document) {
      notifyExceptionListeners(ex, document);
      return PipelineFlowEnum.STOP;
   }

}
