package no.trank.openpipe.opennlp.step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetector;
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
public class ONLPSentenceDetector extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(ONLPSentenceDetector.class);
   public static final String TYPE_SENTENCE = "opennlp.sentence";
   private SentenceDetector detector;
   private List<String> fieldNames = Arrays.asList("text");

   public ONLPSentenceDetector() {
      super("Open NLP Sentence-detector");
   }

   public PipelineStepStatus execute(Document doc) throws PipelineException {
      for (String fieldName : fieldNames) {
         final AnnotatedField field = doc.getField(fieldName);
         if (field != null) {
            field.add(TYPE_SENTENCE, buildAnn(detector.sentPosDetect(field.getValue())));
         } else {
            log.debug("Field '{}' is null", fieldName);
         }
      }
      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
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

   public String getRevision() {
      return "$Revision$";
   }

   public SentenceDetector getDetector() {
      return detector;
   }

   public void setDetector(SentenceDetector detector) {
      this.detector = detector;
   }

   public List<String> getFieldNames() {
      return fieldNames;
   }

   public void setFieldNames(List<String> fieldNames) {
      this.fieldNames = fieldNames;
   }
}