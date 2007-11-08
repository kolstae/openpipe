/*
 * Copyright 2007  T-Rank AS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.trank.openpipe.api;

import java.util.List;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class Pipeline extends BaseSubPipeline {
   private PipelineExceptionHandler pipelineExceptionHandler = new DefaultPipelineExceptionHandler();

   public Pipeline() {
   }

   public Pipeline(List<? extends PipelineStep> pipelineSteps) {
      super(pipelineSteps);
   }

   public PipelineExceptionHandler getPipelineExceptionHandler() {
      return pipelineExceptionHandler;
   }

   public void setPipelineExceptionHandler(PipelineExceptionHandler pipelineExceptionHandler) {
      if (pipelineExceptionHandler != null) {
         this.pipelineExceptionHandler = pipelineExceptionHandler;
      } else {
         throw new NullPointerException("pipelineExceptionHandler can not be null");
      }
   }

   @Override
   public boolean prepare() {
      try {
         return super.prepare();
      } catch (Exception e) {
         return pipelineExceptionHandler.handlePrepareException(wrapToPiplineException(e)).isSuccess();
      }
   }

   @Override
   public void finish(boolean success) {
      try {
         super.finish(success);
      } catch (Exception e) {
         pipelineExceptionHandler.handleFinishException(wrapToPiplineException(e));
      }
   }

   /**
    * Runs one document through the pipeline.
    * 
    * @param document document to be run through the pipeline
    * @return status
    */
   public PipelineFlow execute(Document document) {
      try {
         executeSteps(document);
         return PipelineFlowEnum.CONTINUE;
      } catch (Exception e) {
         return pipelineExceptionHandler.handleDocumentException(wrapToPiplineException(e), document);
      }
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
            PipelineFlow pipelineFlow = execute(document);
            if (pipelineFlow.isStopPipeline()) {
               return pipelineFlow.isSuccess();
            }
         }
         return true;
      } catch (Exception e) {
         return pipelineExceptionHandler.handleProducerException(wrapToPiplineException(e)).isSuccess();
      }
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
         success = prepare();
         if (success) {
            success = execute(documents);
         }
      } finally {
         finish(success);
      }
      return success;
   }

   private PipelineException wrapToPiplineException(Exception ex) {
      PipelineException pex;
      if (PipelineException.class.isAssignableFrom(ex.getClass())) {
         pex = (PipelineException) ex;
      } else {
         pex = new PipelineException(ex);
      }
      return pex;
   }
}
