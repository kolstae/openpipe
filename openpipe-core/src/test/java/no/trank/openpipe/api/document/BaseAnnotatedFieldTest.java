/*
 * Copyright 2007  T-Rank AS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.trank.openpipe.api.document;

import java.util.ArrayList;
import java.util.Arrays;
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

   public void testSet() throws Exception {
      final BaseAnnotation ann = new BaseResolvedAnnotation(new BaseAnnotation(), "");
      field.set(PRESENT, Arrays.asList(ann));
      assertSame(ann, field.iterator(PRESENT).next());
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
