package no.trank.openpipe.api;

import java.util.Collections;
import java.util.List;

import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public abstract class MultiInputFieldPipelineStep extends BasePipelineStep {
   private List<String> inputFields = Collections.emptyList();

   /**
    * Creates a step with the given name.
    *
    * @param name the name of step.
    *
    * @see PipelineStep#getName()
    * @see PipelineStep#setName(String)
    */
   public MultiInputFieldPipelineStep(String name) {
      super(name);
   }

   /**
    * Executes {@link #executeInputFields(Document)}.
    * 
    * @return <tt>PipelineStepStatus.DEFAULT</tt>.
    */
   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      executeInputFields(doc);
      return PipelineStepStatus.DEFAULT;
   }

   /**
    * Executes {@link #process(Document, String, List)} for all doc fields in {@link #getInputFields() inputFields} that 
    * aren't empty.   
    * 
    * @param doc the document to execute on.
    * 
    * @throws PipelineException if thrown by {@link # process (Document, String, List)}.
    */
   protected void executeInputFields(Document doc) throws PipelineException {
      for (String fieldName : inputFields) {
         final List<AnnotatedField> fields = doc.getFields(fieldName);
         if (!fields.isEmpty()) {
            process(doc, fieldName, fields);
         }
      }
   }

   /**
    * Processes the field-values for a given field.
    * 
    * @param doc the document this field-value belongs to.
    * @param fieldName the name of the field.
    * @param fieldValues the values of the field.
    * 
    * @throws PipelineException if an error occures.
    * 
    * @see #executeInputFields(Document)
    */
   protected abstract void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) 
         throws PipelineException;

   /**
    * Checks if {@link #getInputFields() inputFields} is empty. 
    * 
    * @throws PipelineException if {@link #getInputFields() inputFields} is empty.
    */
   @Override
   public void prepare() throws PipelineException {
      if (inputFields.isEmpty()) {
         throw new PipelineException("No input-fields configured");
      }
   }

   /**
    * Gets the input-fields for this step.
    * 
    * @return the input-fields for this step. <em>Must</em> not be <tt>null</tt>.
    */
   public List<String> getInputFields() {
      return inputFields;
   }

   /**
    * Sets the input-fields for this step. <tt>null</tt> will be replaced with {@link Collections#emptyList()}.
    * 
    * @param inputFields the input-fields for this step.
    */
   public void setInputFields(List<String> inputFields) {
      if (inputFields != null) {
         this.inputFields = inputFields;
      } else {
         this.inputFields = Collections.emptyList();
      }
   }

   /**
    * Sets the input-field for this step. Calls <tt>setInputFields(Collections.singletonList(inputField))</tt>.
    * 
    * @param inputField the input-field for this step.
    */
   public void setInputField(String inputField) {
      setInputFields(Collections.singletonList(inputField));
   }
}
