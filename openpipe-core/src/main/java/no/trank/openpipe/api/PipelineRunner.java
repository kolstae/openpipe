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
            if (pipeline.prepare()) {
               success = pipeline.execute(documentProducer);
            }
         } finally {
            pipeline.finish(success); // Todo: Fix finsih(true/false) ???????
         }
      } finally {
         documentProducer.close();
      }
   }
}
