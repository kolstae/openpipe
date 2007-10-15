package no.trank.openpipe;

import no.trank.openpipe.api.Pipeline;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentProducer;

/**
 *
 * @version $Revision$

 */
public class PipelineRunner implements Runnable {
   private Pipeline pipeline;
   private DocumentProducer documentProducer;

   public void setPipeline(Pipeline pipeline) {
      this.pipeline = pipeline;
   }

   public void setDocumentReader(DocumentProducer documentProducer) {
      this.documentProducer = documentProducer;
   }

   public void run() {
      try {
         documentProducer.init();
         if (pipeline.prepare()) {
            try {
               for (Document document : documentProducer) {
                  pipeline.execute(document);
               }
               pipeline.finish(true); // Todo: Fix finsih(true/false) ???????
            } catch (Exception e) {
               pipeline.getPipelineErrorHandler().handleException(true, new PipelineException(e));
               pipeline.finish(false);
            }
         }
      } finally {
         documentProducer.close();
      }
   }
}
