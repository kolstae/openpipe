package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.BaseAnnotation;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class AnnotateSpace extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(AnnotateSpace.class);

   private List<String> fieldNames = Collections.emptyList();

   public AnnotateSpace() {
      super("AnnotateSpace");
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   @Override
   public PipelineStepStatus execute(Document doc) {
      if(!fieldNames.isEmpty()) {
         for (String fieldName : fieldNames) {
            process(doc, fieldName);
         }
      }

      return PipelineStepStatus.DEFAULT;
   }

   private static void process(Document doc, String fieldName) {
      final AnnotatedField field = doc.getField(fieldName);
      final String text = field.getValue();
      if (text == null) {
         log.debug("Field '{}' - null", fieldName);
      } else {
         List<Annotation> annotations = new ArrayList<Annotation>();
         
         int i = -1;
         while((i = text.indexOf(' ', i+1)) != -1) {
            annotations.add(new BaseAnnotation(i, i+1));
         }
         
         log.debug("Field '{}'  space annotations: {}", fieldName, annotations.size());
         field.add("space", annotations);
      }
   }

   public void setFieldNames(List<String> fieldNames) {
      this.fieldNames = fieldNames;
   }

   public List<String> getFieldNames() {
      return fieldNames;
   }
}