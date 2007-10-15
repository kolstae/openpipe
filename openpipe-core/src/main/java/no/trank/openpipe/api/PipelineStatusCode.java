package no.trank.openpipe.api;

/**
 * @version $Revision$
 */
public enum PipelineStatusCode {
   CONTINUE(false),
   FINISH(true);

   private boolean done;

   PipelineStatusCode(boolean done) {
      this.done = done;
   }

   public boolean isDone() {
      return done;
   }
}
