package no.trank.openpipe.api;

/**
 *
 * The pipelineflow is used to control flow at exceptions in the pipeline.
 *
 * @see no.trank.openpipe.api.PipelineFlowEnum
 * @see no.trank.openpipe.api.PipelineExceptionHandler
 *  
 * @version $Revision$
 */
public interface PipelineFlow {

   /**
    * Indicates if the pipeline operations should be stopped.(Aborted)
    * @return <tt>true</tt> to stop, false otherwise
    */
   public boolean isStopPipeline();

   /**
    * Indicates if the operation was a success. This will be used when calling <tt>finish(boolean)</tt> on the
    * pipeline-steps.
    * @return <tt>true</tt> if everything is ok, false otherwise
    */
   public boolean isSuccess();
}
