package no.trank.openpipe.api;

/**
 * A convenient base-implementation of a {@link PipelineStep}.
 *
 * @version $Revision$
 */
public abstract class BasePipelineStep implements PipelineStep {
   private String name;

   /**
    * Creates a step with the given name.
    *
    * @param name the name of step.
    *
    * @see PipelineStep#getName()
    * @see PipelineStep#setName(String)
    */
   public BasePipelineStep(String name) {
      this.name = name;
   }

   /**
    * Does nothing. Override to implement. 
    */
   public void prepare() throws PipelineException {
   }

   /**
    * Does nothing. Override to implement.
    */
   public void finish(boolean success) throws PipelineException {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
