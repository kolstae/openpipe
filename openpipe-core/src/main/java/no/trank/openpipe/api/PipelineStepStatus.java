package no.trank.openpipe.api;

/**
 * @version $Revision$
 */
public class PipelineStepStatus {
   public static final PipelineStepStatus DEFAULT = new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   private final PipelineStepStatusCode stepStatusCode;
   private final SubPipeline subPipeline;

   public PipelineStepStatus(PipelineStepStatusCode stepStatusCode) {
      this(stepStatusCode, null);
   }

   public PipelineStepStatus(PipelineStepStatusCode stepStatusCode, SubPipeline subPipeline) {
      this.stepStatusCode = stepStatusCode;
      this.subPipeline = subPipeline;
   }

   public PipelineStepStatusCode getStatusCode() {
      return stepStatusCode;
   }

   public SubPipeline getSubPipeline() {
      return subPipeline;
   }

   @Override
   public String toString() {
      return "PipelineStepStatus{" +
            "stepStatusCode=" + stepStatusCode +
            ", subPipeline=" + subPipeline +
            '}';
   }
}
