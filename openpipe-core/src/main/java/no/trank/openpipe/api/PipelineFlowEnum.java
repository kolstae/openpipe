package no.trank.openpipe.api;

/**
 * @version $Revision$
 */
public enum PipelineFlowEnum implements PipelineFlow {
   STOP(true, false),
   STOP_AND_SUCCESS(true, true),
   CONTINUE(false, true);

   private boolean stopPipeline = false;
   private boolean success = true;

   PipelineFlowEnum(boolean stopPipeline, boolean success) {
      this.stopPipeline = stopPipeline;
      this.success = success;
   }

   public boolean isStopPipeline() {
      return stopPipeline;
   }

   public boolean isSuccess() {
      return success;
   }
}
