package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.BaseAnnotation;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class AnnotateSentence extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(AnnotateSpace.class);
   public static final String SENTENCE = "sentence";
   private static final char[] CHARS_SENT = new char[] {'.', '!', '?'};
   private static final char[] CHARS_WS = new char[] {' ', '"', '\r', '\n', '\t'};
   private List<String> fieldNames = Collections.emptyList();

   public AnnotateSentence() {
      super("AnnotateSentence");
   }

   static {
      Arrays.sort(CHARS_SENT);
      Arrays.sort(CHARS_WS);
   }
   
   @Override
   public String getRevision() {
      return "$Revision$";
   }

   @Override
   public PipelineStepStatus execute(Document doc) {
      for (String fieldName : fieldNames) {
         process(doc, fieldName);
      }

      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   private static void process(Document doc, String fieldName) {
      final AnnotatedField field = doc.getField(fieldName);
      final String text = field.getValue();
      if (text == null) {
         log.debug("Field '{}' - null", fieldName);
      } else {
         List<Annotation> annotations = new ArrayList<Annotation>();
         
         int from = -1;
         for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            if (from < 0) {
               if (!isWhiteSpace(c)) {
                  from = i;
               } else {
                  continue;
               }
            }
            if (Arrays.binarySearch(CHARS_SENT, c) >= 0 && from < i - 1) {
               if (i < text.length() - 1 && isWhiteSpace(text.charAt(i + 1))) {
                  annotations.add(new BaseAnnotation(from, i));
                  from = -1;
               }
            }
         }
         if (from >= 0 && from < text.length() - 1) {
            annotations.add(new BaseAnnotation(from, text.length()));
         }
         
         log.debug("Field '{}'  sentence annotations: {}", fieldName, annotations.size());
         field.add(SENTENCE, annotations);
      }
   }

   private static boolean isWhiteSpace(char c) {
      return Arrays.binarySearch(CHARS_WS, c) >= 0;
   }

   public void setFieldNames(List<String> fieldNames) {
      this.fieldNames = fieldNames;
   }

   public List<String> getFieldNames() {
      return fieldNames;
   }

   @Override
   public void prepare() throws PipelineException {
      if (fieldNames.isEmpty()) {
         throw new PipelineException("No field-names configured");
      }
   }
}