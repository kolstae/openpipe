package no.trank.openpipe.api;

/**
 * @version $Revision: 874 $
 */
public class PipelineStepStatus {
   private PipelineStepStatusCode stepStatusCode;
   private String statusDescription;
   private SubPipeline subPipeline;

   public PipelineStepStatus() {
   }

   public PipelineStepStatus(PipelineStepStatusCode stepStatusCode) {
      this.stepStatusCode = stepStatusCode;
   }

   public PipelineStepStatusCode getStatusCode() {
      return stepStatusCode;
   }

   public void setStatusCode(PipelineStepStatusCode stepStatusCode) {
      this.stepStatusCode = stepStatusCode;
   }

   public String getStatusDescription() {
      return statusDescription;
   }

   public void setStatusDescription(String statusDescription) {
      this.statusDescription = statusDescription;
   }

   public SubPipeline getSubPipeline() {
      return subPipeline;
   }

   public void setSubPipeline(SubPipeline subPipeline) {
      this.subPipeline = subPipeline;
   }

   @Override
   public String toString() {
      return "PipelineStepStatus{" +
            "stepStatusCode=" + stepStatusCode +
            ", statusDescription='" + statusDescription + '\'' +
            ", subPipeline=" + subPipeline +
            '}';
   }
}
