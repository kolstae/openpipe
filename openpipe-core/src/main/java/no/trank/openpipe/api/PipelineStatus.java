package no.trank.openpipe.api;

/**
 */
public class PipelineStatus {
   private final int status;

   public PipelineStatus(int status) {
      this.status = status;
   }

   public int getStatus() {
      return status;
   }
}
