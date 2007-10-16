package no.trank.openpipe.api;

import java.util.List;

import no.trank.openpipe.api.document.Document;

/**
 * An interface the represents a sub-pipeline. Differs from {@link Pipeline} in that it does not have an error-handler.
 *
 * @version $Revision$
 */
public interface SubPipeline {
   
   /**
    * Prepares the sub-pipeline for a batch of documents. Must be called before the first call to
    * {@link #executeSteps(Document)}.
    *
    * @return <tt>true</tt> if the sub-pipeline was succesfully prepared, otherwise <tt>false</tt>.
    *
    * @throws PipelineException if the sub-pipeline could not be prepared.
    */
   boolean prepare() throws PipelineException;
   
   /**
    * Finishes the sub-pipeline after a batch of documents. Must be called after a batch of documents have been
    * processed with {@link #executeSteps(Document)}, to ensure changes are committed or resources are released.
    *
    * @param success if the previous batch of documents succeeded.
    *
    * @throws PipelineException if the sub-pipeline could not finish successfully.
    */
   void finish(boolean success) throws PipelineException;

   /**
    * Gets a list of the steps involved in this sub-pipeline.
    *
    * @return a list of the steps involved in this sub-pipeline.
    */
   List<? extends PipelineStep> getPipelineSteps();

   /**
    * Sets a list of the steps involved in this sub-pipeline.
    *
    * @param pipelineSteps the list of the steps involved in this sub-pipeline.
    */
   void setPipelineSteps(List<? extends PipelineStep> pipelineSteps);

   /**
    * Executes the steps of the sub-pipeline on the specified document. {@link #prepare()} must be called before the
    * first call to <tt>executeSteps()</tt>.
    *
    * @param document the document to process.
    *
    * @return a status-code describing whether to continue processing this document or not.
    *
    * @throws PipelineException if the sub-pipeline failed to process the given document.
    */
   PipelineStatusCode executeSteps(Document document) throws PipelineException;
}
