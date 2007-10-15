package no.trank.openpipe.opennlp.step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.Document;

/**

 * @version $Revision$
 */
public class ONLPTokenizer extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(ONLPTokenizer.class);
   public static final String TYPE_TOKENIZE = "opennlp.tokenize";
   private Tokenizer tokenizer;
   private List<String> fieldNames = Arrays.asList("text");

   public ONLPTokenizer() {
      super("Open NLP Tokenizer");
   }

   public PipelineStepStatus execute(Document doc) throws PipelineException {
      for (String fieldName : fieldNames) {
         final AnnotatedField field = doc.getField(fieldName);
         if (field != null) {
            field.add(TYPE_TOKENIZE, buildAnn(tokenizer.tokenizePos(field.getValue())));
         } else {
            log.debug("Field '{}' is null", fieldName);
         }
      }
      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   private static List<Annotation> buildAnn(Span[] spans) {
      final List<Annotation> list = new ArrayList<Annotation>(spans.length);
      for (Span span : spans) {
         list.add(new SpanAnnotation(span));
      }
      return list;
   }

   public String getRevision() {
      return "$Revision$";
   }

   public Tokenizer getTokenizer() {
      return tokenizer;
   }

   public void setTokenizer(Tokenizer tokenizer) {
      this.tokenizer = tokenizer;
   }

   public List<String> getFieldNames() {
      return fieldNames;
   }

   public void setFieldNames(List<String> fieldNames) {
      this.fieldNames = fieldNames;
   }

   private static final class SpanAnnotation implements Annotation {
      private final Span span;

      public SpanAnnotation(Span span) {
         this.span = span;
      }

      public int getStartPos() {
         return span.getStart();
      }

      public int getEndPos() {
         return span.getEnd();
      }
   }
}
