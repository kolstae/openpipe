package no.trank.openpipe.api;

/**
 * @version $Revision$
 */
public abstract class BasePipelineStep implements PipelineStep {
   private String name;

   public BasePipelineStep(String name) {
      this.name = name;
   }

   public void prepare() throws PipelineException {
   }

   public void finish(boolean success) throws PipelineException {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
