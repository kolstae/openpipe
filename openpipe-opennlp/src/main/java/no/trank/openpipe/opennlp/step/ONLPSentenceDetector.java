package no.trank.openpipe.opennlp.step;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetector;

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.BaseAnnotation;
import no.trank.openpipe.api.document.Document;

/**

 * @version $Revision$
 */
public class ONLPSentenceDetector extends MultiInputFieldPipelineStep {
   public static final String TYPE_SENTENCE = "opennlp.sentence";
   private SentenceDetector detector;

   public ONLPSentenceDetector() {
      super("Open NLP Sentence-detector");
   }

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      for (AnnotatedField fieldValue : fieldValues) {
         fieldValue.add(TYPE_SENTENCE, buildAnn(detector.sentPosDetect(fieldValue.getValue())));
      }
   }

   @Override
   public void prepare() throws PipelineException {
      super.prepare();
      
      if (detector == null) {
         throw new PipelineException("No detector configured");
      }
   }

   private static List<Annotation> buildAnn(int[] offsets) {
      final List<Annotation> list = new ArrayList<Annotation>(offsets.length);
      int lastOffset = 0;
      for (int offset : offsets) {
         list.add(new BaseAnnotation(lastOffset, offset));
         lastOffset = offset;
      }
      return list;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public SentenceDetector getDetector() {
      return detector;
   }

   public void setDetector(SentenceDetector detector) {
      this.detector = detector;
   }
}