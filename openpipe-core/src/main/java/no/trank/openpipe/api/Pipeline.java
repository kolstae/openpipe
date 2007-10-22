package no.trank.openpipe.api;

import java.util.List;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class Pipeline extends BaseSubPipeline {
   private PipelineErrorHandler pipelineErrorHandler = new LoggingPipelineErrorHandler();

   public Pipeline() {
   }

   public Pipeline(List<? extends PipelineStep> pipelineSteps) {
      super(pipelineSteps);
   }

   @Override
   public boolean prepare() {
      try {
         return super.prepare();
      } catch (PipelineException e) {
         pipelineErrorHandler.handleException(false, e);
      } catch (RuntimeException e) {
         pipelineErrorHandler.handleException(false, new PipelineException(e));
      }
      return false;
   }

   @Override
   public void finish(boolean success) {
      try {
         super.finish(success);
      } catch (PipelineException e) {
         pipelineErrorHandler.handleException(true, e);
      } catch (RuntimeException e) {
         pipelineErrorHandler.handleException(true, new PipelineException(e));
      }
   }

   /**
    * Runs one document through the pipeline.
    * 
    * @param document document to be run through the pipeline
    * @return status
    */
   public PipelineStatusCode execute(Document document) {
      try {
         return executeSteps(document);
      } catch (PipelineException e) {
         pipelineErrorHandler.handleException(document, e);
      } catch (RuntimeException e) {
         pipelineErrorHandler.handleException(document, new PipelineException(e));
      }
      return PipelineStatusCode.FINISH;
   }

   /**
    * Runs a batch of documents through the pipeline.
    * 
    * @param documents documents to be run through the pipeline
    * @return success indicator
    */
   public boolean execute(Iterable<Document> documents) {
      try {
         for (Document document : documents) {
            execute(document);
         }
         return true;
      } catch (Exception e) {
         pipelineErrorHandler.handleException(false, new PipelineException(e));
      }
      return false;
   }
   
   /**
    * Runs a batch of documents through the pipeline. Also handles initializing and closing of the pipeline.
    * 
    * @param documents documents to be run through the pipeline
    * @return success indicator
    */
   public boolean run(Iterable<Document> documents) {
      boolean success = false;
      try {
         if (prepare()) {
            success = execute(documents);
         }
      } finally {
         finish(success);
      }
      return success;
   }

   public PipelineErrorHandler getPipelineErrorHandler() {
      return pipelineErrorHandler;
   }

   public void setPipelineErrorHandler(PipelineErrorHandler pipelineErrorHandler) {
      if (pipelineErrorHandler != null) {
         this.pipelineErrorHandler = pipelineErrorHandler;
      } else {
         this.pipelineErrorHandler = new LoggingPipelineErrorHandler();
      }
   }
}
