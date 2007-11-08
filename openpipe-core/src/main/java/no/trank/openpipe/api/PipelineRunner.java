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

import no.trank.openpipe.api.document.DocumentProducer;

/**
 * @version $Revision$
 */
public class PipelineRunner implements Runnable {
   private Pipeline pipeline;
   private DocumentProducer documentProducer;

   public Pipeline getPipeline() {
      return pipeline;
   }

   public void setPipeline(Pipeline pipeline) {
      this.pipeline = pipeline;
   }

   public DocumentProducer getDocumentProducer() {
      return documentProducer;
   }

   public void setDocumentReader(DocumentProducer documentProducer) {
      this.documentProducer = documentProducer;
   }

   @Override
   public void run() {
      boolean success = false;
      try {
         documentProducer.init();
         try {
            if (PipelineExceptionListener.class.isAssignableFrom(documentProducer.getClass())) {
               pipeline.getPipelineExceptionHandler().addExceptionListener((PipelineExceptionListener) documentProducer);
            }
            success = pipeline.prepare();
            if (success) {
               success = pipeline.execute(documentProducer);
            }
         } finally {
            pipeline.finish(success);
         }
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
