package no.trank.openpipe.api;

/**
 * @version $Revision$
 */
public enum PipelineStatusCode {
   /**
    * Signals <tt>continue</tt> to the pipeline. {@link PipelineStatusCode#isDone() CONTINUE.isDone()} <tt>== false</tt>
    */
   CONTINUE(false),

   /**
    * Signals <tt>finish</tt> to the pipeline. {@link PipelineStatusCode#isDone() FINISH.isDone()} <tt>== true</tt>
    */
   FINISH(true);

   private boolean done;

   PipelineStatusCode(boolean done) {
      this.done = done;
   }

   /**
    * Gets whether this status signals <tt>done</tt>.
    *
    * @return <tt>true</tt> if this status signals <tt>done</tt>, otherwise <tt>false</tt>.
    */
   public boolean isDone() {
      return done;
   }
}
