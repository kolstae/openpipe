package no.trank.openpipe.api.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class BaseAnnotatedFieldTest extends TestCase {
   private static final String VALUE = "en gul katt virre virre vapp";
   private static final String[] WORDS = VALUE.split(" ");
   private static final String NOT_PRESENT = "dillbert";
   private static final String PRESENT = "word";
   private BaseAnnotatedField field;

   public void testAdd() throws Exception {
      assertTrue(field.add(NOT_PRESENT, Collections.<Annotation>emptyList()));
      assertFalse(field.add(PRESENT, Collections.<Annotation>emptyList()));
   }

   public void testIterate() throws Exception {
      assertNotNull(field.iterator(NOT_PRESENT));
      assertFalse(field.iterator(NOT_PRESENT).hasNext());
      final Iterator<ResolvedAnnotation> iterator = field.iterator(PRESENT);
      assertNotNull(iterator);
      for (String word : WORDS) {
         assertTrue(iterator.hasNext());
         assertEquals(word, iterator.next().getValue());
      }
      assertFalse(iterator.hasNext());
   }

   @Override
   protected void setUp() throws Exception {
      final HashMap<String, List<? extends Annotation>> map = new HashMap<String, List<? extends Annotation>>();
      final ArrayList<Annotation> ann = new ArrayList<Annotation>();
      for (String word : WORDS) {
         final int idx = VALUE.indexOf(word);
         ann.add(new BaseAnnotation(idx, idx + word.length()));
      }
      map.put(PRESENT, ann);
      field = new BaseAnnotatedField(VALUE, map);
   }
}
