package no.trank.openpipe.opennlp.step;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.Document;

/**

 * @version $Revision$
 */
public class ONLPTokenizer extends MultiInputFieldPipelineStep {
   public static final String TYPE_TOKENIZE = "opennlp.tokenize";
   private Tokenizer tokenizer;

   public ONLPTokenizer() {
      super("Open NLP Tokenizer");
   }

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      for (AnnotatedField fieldValue : fieldValues) {
         fieldValue.add(TYPE_TOKENIZE, buildAnn(tokenizer.tokenizePos(fieldValue.getValue())));
      }
   }

   @Override
   public void prepare() throws PipelineException {
      super.prepare();
      
      if (tokenizer == null) {
         throw new PipelineException("No tokenizer configured");
      }
   }

   private static List<Annotation> buildAnn(Span[] spans) {
      final List<Annotation> list = new ArrayList<Annotation>(spans.length);
      for (Span span : spans) {
         list.add(new SpanAnnotation(span));
      }
      return list;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public Tokenizer getTokenizer() {
      return tokenizer;
   }

   public void setTokenizer(Tokenizer tokenizer) {
      this.tokenizer = tokenizer;
   }

   private static final class SpanAnnotation implements Annotation {
      private final Span span;

      public SpanAnnotation(Span span) {
         this.span = span;
      }

      @Override
      public int getStartPos() {
         return span.getStart();
      }

      @Override
      public int getEndPos() {
         return span.getEnd();
      }
   }
}
