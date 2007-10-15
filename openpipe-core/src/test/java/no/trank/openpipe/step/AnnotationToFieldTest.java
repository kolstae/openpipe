package no.trank.openpipe.step;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.BaseAnnotatedField;
import no.trank.openpipe.api.document.BaseAnnotation;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class AnnotationToFieldTest extends TestCase {
   private static final String SENTENCE = "sentence";
   private static final String TEXT = "text";
   private AnnotationToField atf;

   public void testExecute() throws Exception {
      final Document doc = new Document();
      final HashMap<String, List<? extends Annotation>> map = new HashMap<String, List<? extends Annotation>>();
      final String value = "Dette er en test. Det er dette også!";
      final int idx = value.indexOf('.') + 1;
      map.put(SENTENCE, Arrays.<Annotation>asList(new BaseAnnotation(0, idx), new BaseAnnotation(idx, value.length())));
      doc.setField(TEXT, new BaseAnnotatedField(value, map));
      atf.execute(doc);
      final List<String> list = doc.getFieldValues(SENTENCE);
      assertEquals(2, list.size());
      assertEquals(value.substring(0, idx), list.get(0));
      assertEquals(value.substring(idx), list.get(1));
   }

   public void testEmpty() throws Exception {
      final Document doc = new Document();
      atf.setFailOnEmpty(true);
      try {
         atf.execute(doc);
         fail("Should throw PE");
      } catch (PipelineException e) {
         // Ignoring
      }
      atf.setFailOnEmpty(false);
      atf.execute(doc);
      assertTrue(doc.getFieldValues(SENTENCE).isEmpty());
   }

   @Override
   protected void setUp() throws Exception {
      atf = new AnnotationToField();
      atf.setAnnotationType(SENTENCE);
      atf.setFromFieldName(TEXT);
      atf.setToFieldName(SENTENCE);
   }
}
