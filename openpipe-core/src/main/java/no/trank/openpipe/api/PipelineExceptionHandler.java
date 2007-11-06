package no.trank.openpipe.api;

import no.trank.openpipe.api.document.Document;

/**
 * This class is a strategy for handling exceptions in the pipeline.
 *
 * <b>Note:</b> All implementations of this should notify exception listeners
 *
 *
 * @see no.trank.openpipe.api.DefaultPipelineExceptionHandler
 * @see no.trank.openpipe.api.BasePipelineExceptionHandler
 *
 * @version $Revision$
 */
public interface PipelineExceptionHandler {
   /**
    * An exception was thrown in pipeline prepare.
    *
    * @param ex the exception that was thrown
    * @return what the pipeline should do about this
    */
   PipelineFlow handlePrepareException(PipelineException ex);

   /**
    * An exception was thrown in pipeline finish.
    *
    * @param ex the exception that was thrown
    */
   void handleFinishException(PipelineException ex);

   /**
    * An ecxeption was thrown by the producer iterable.
    *
    * @param ex the exception that was thrown.
    * @return what the pipeline should do about this
    */
   PipelineFlow handleProducerException(PipelineException ex);

   /**
    * An exception was thrown on one document in the pipeline.
    *
    * @param ex the exception that was thrown.
    * @param document the document that triggered the exception.
    * @return what the pipeline should do about this
    */
   PipelineFlow handleDocumentException(PipelineException ex, Document document);

   /**
    * Add an exception listener that should be notified on every exception generated.
    *
    * @param exceptionListener the exception listener taht will be notified.
    */
   void addExceptionListener(PipelineExceptionListener exceptionListener);


   /**
    * Remove exception listener from the list that is notified on exception
    *
    * @param exceptionListener the listener to remove
    */
   void removeExceptionListener(PipelineExceptionListener exceptionListener);

}
