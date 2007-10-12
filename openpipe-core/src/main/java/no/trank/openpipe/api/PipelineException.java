package no.trank.openpipe.api;

/**
 * @version $Revision: 874 $
 */
public class PipelineException extends Exception {
   private String pipelineStepName;

   public PipelineException() {
      super();
   }

   public PipelineException(String message) {
      this(message, null, null);
   }
   
   public PipelineException(String message, String pipelineStepName) {
      this(message, null, pipelineStepName);
   }

   public PipelineException(String message, Throwable cause) {
      this(message, cause, null);
   }
   
   public PipelineException(String message, Throwable cause, String pipelineStepName) {
      super(message, cause);
      this.pipelineStepName = pipelineStepName;
   }

   public PipelineException(Throwable cause) {
      this(null, cause, null);
   }
   
   public PipelineException(Throwable cause, String pipelineStepName) {
      this(null, cause, pipelineStepName);
   }

   public String getPipelineStepName() {
      return pipelineStepName;
   }

   public void setPipelineStepName(String pipelineStepName) {
      this.pipelineStepName = pipelineStepName;
   }

   public void setPipelineStepNameIfNull(String pipelineStepName) {
      if (this.pipelineStepName == null) {
         setPipelineStepName(pipelineStepName);
      }
   }

   @Override
   public String getMessage() {
      return pipelineStepName == null ? super.getMessage() : pipelineStepName + ": " + super.getMessage();
   }
}
