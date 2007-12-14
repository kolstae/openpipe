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
      boolean success = false;
      try {
         documentProducer.init();
         if (PipelineExceptionListener.class.isAssignableFrom(documentProducer.getClass())) {
            pipeline.getPipelineExceptionHandler().addExceptionListener((PipelineExceptionListener) documentProducer);
         }
         success = pipeline.run(documentProducer);
      } finally {
         if (PipelineExceptionListener.class.isAssignableFrom(documentProducer.getClass())) {
            pipeline.getPipelineExceptionHandler().removeExceptionListener((PipelineExceptionListener) documentProducer);
         }
         if (success) {
            documentProducer.close();
         } else {
            documentProducer.fail();
         }
      }
   }
}