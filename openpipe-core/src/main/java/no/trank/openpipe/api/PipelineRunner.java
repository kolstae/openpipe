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

import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentProducer;

/**
 * A <code>PipelineRunner</code> instance requests {@link Document}s from a {@link DocumentProducer} and feeds
 * them into a {@link Pipeline}.
 * 
 * <p>If the {@link DocumentProducer} implements {@link PipelineExceptionListener}, it is added to the
 * {@link Pipeline}'s {@link PipelineExceptionHandler} before feeding the {@link Document}s, and removed after.    
 * 
 * @version $Revision$
 */
public class PipelineRunner implements Runnable {
   private Pipeline pipeline;
   private DocumentProducer documentProducer;

   /**
    * Gets the pipeline that the {@link Document}s are fed into.
    * 
    * @return the pipeline
    */
   public Pipeline getPipeline() {
      return pipeline;
   }

   /**
    * Sets the pipeline that the {@link Document}s are fed into.
    * 
    * @param pipeline the pipeline
    */
   public void setPipeline(Pipeline pipeline) {
      this.pipeline = pipeline;
   }

   /**
    * Gets the {@link DocumentProducer} that produces the {@link Document}s.
    * 
    * @return the document producer
    */
   public DocumentProducer getDocumentProducer() {
      return documentProducer;
   }

   /**
    * Sets the {@link DocumentProducer} that produces the {@link Document}s.
    * 
    * @param documentProducer the document producer
    */
   public void setDocumentReader(DocumentProducer documentProducer) {
      this.documentProducer = documentProducer;
   }

   @Override
   public void run() {
      runPipeline(documentProducer, pipeline);
   }

   /**
    * Runs a pipeline with a given producer.
    * <br/>
    * Full cycle of {@link DocumentProducer#init()},
    * {@link DocumentProducer#close()}/{@link DocumentProducer#fail()} is called on <tt>producer</tt>.
    *
    * @param producer the <tt>DocumentProducer</tt> for the run.
    * @param pipeline the pipeline to run.
    *
    * @return <tt>true</tt> only if {@link Pipeline#run(Iterable) Pipeline.run(producer)} returned <tt>true</tt>,
    * otherwise <tt>false</tt>.
    */
   public static boolean runPipeline(DocumentProducer producer, Pipeline pipeline) {
      boolean success = false;
      try {
         producer.init();
         if (PipelineExceptionListener.class.isAssignableFrom(producer.getClass())) {
            pipeline.getPipelineExceptionHandler().addExceptionListener((PipelineExceptionListener) producer);
         }
         success = pipeline.run(producer);
      } finally {
         if (PipelineExceptionListener.class.isAssignableFrom(producer.getClass())) {
            pipeline.getPipelineExceptionHandler().removeExceptionListener((PipelineExceptionListener) producer);
         }
         if (success) {
            producer.close();
         } else {
            producer.fail();
         }
      }
      return success;
   }
}