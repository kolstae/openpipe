package no.trank.openpipe.api;

/**
 */
public enum PipelineStepStatusCode {
   CONTINUE(PipelineStatusCode.CONTINUE, false),
   DIVERT_PIPELINE(PipelineStatusCode.CONTINUE, true),
   OVERRIDE_PIPELINE(PipelineStatusCode.FINISH, true),
   FINISH(PipelineStatusCode.FINISH, false);

   private final PipelineStatusCode statusCode;
   private final boolean subPipeline;

   PipelineStepStatusCode(PipelineStatusCode statusCode, boolean subPipeline) {
      this.statusCode = statusCode;
      this.subPipeline = subPipeline;
   }

   public boolean isDone() {
      return statusCode.isDone();
   }

   public boolean hasSubPipeline() {
      return subPipeline;
   }
   
   public PipelineStatusCode toPipelineStatusCode() {
      return statusCode;
   }
}
