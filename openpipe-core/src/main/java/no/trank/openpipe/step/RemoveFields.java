package no.trank.openpipe.step;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class RemoveFields extends MultiInputFieldPipelineStep {
   private static final Logger log = LoggerFactory.getLogger(RemoveFields.class);

   public RemoveFields() {
      super("RemoveField");
   }

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      log.debug("Removing field '{}'", fieldName);
      doc.removeField(fieldName);
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }
}