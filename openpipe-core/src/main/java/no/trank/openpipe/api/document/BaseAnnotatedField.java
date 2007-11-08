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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import no.trank.openpipe.util.Iterators;

/**
 * A basic implementation of {@link AnnotatedField}.
 * 
 * @version $Revision$
 */
public class BaseAnnotatedField implements AnnotatedField {
   private final Map<String, List<? extends Annotation>> annotations = new LinkedHashMap<String, List<? extends Annotation>>();
   private String value;

   /**
    * Constructs a <tt>BaseAnnotatedField</tt> with a value of <tt>null</tt>.
    */
   public BaseAnnotatedField() {
   }

   /**
    * Constructs a <tt>BaseAnnotatedField</tt> with a value of <tt>value</tt>.
    */
   public BaseAnnotatedField(String value) {
      this.value = value;
   }

   /**
    * Constructs a <tt>BaseAnnotatedField</tt> with a value of <tt>value</tt> and the given annotations.
    */
   public BaseAnnotatedField(String value, Map<String, ? extends List<? extends Annotation>> annotations) {
      this.value = value;
      this.annotations.putAll(annotations);
   }

   @Override
   public String getValue() {
      return value;
   }

   /**
    * Sets the value of this field.
    * 
    * @param value the new value of this field.
    */
   public void setValue(String value) {
      this.value = value;
   }

   @Override
   public Set<String> getAnnotationTypes() {
      return Collections.unmodifiableSet(annotations.keySet());
   }

   @Override
   public boolean add(String type, List<? extends Annotation> annotations) {
      final boolean res = !this.annotations.containsKey(type);
      if (res) {
         set(type, annotations);
      }
      return res;
   }

   @Override
   public void set(String type, List<? extends Annotation> annotations) {
      this.annotations.put(type, Collections.unmodifiableList(annotations));
   }

   @Override
   public ListIterator<ResolvedAnnotation> iterator(String type) {
      final List<? extends Annotation> list = annotations.get(type);
      if (list != null && !list.isEmpty()) {
         return new BaseAnnotationIterator(value, list.listIterator());
      }
      return Iterators.emptyIterator();
   }

   private static class BaseAnnotationIterator implements ListIterator<ResolvedAnnotation> {
      private final String fieldValue;
      private final ListIterator<? extends Annotation> iterator;

      private BaseAnnotationIterator(String value, ListIterator<? extends Annotation> it) {
         fieldValue = value;
         iterator = it;
      }

      @Override
      public boolean hasNext() {
         return iterator.hasNext();
      }

      @Override
      public ResolvedAnnotation next() {
         return toResolvedAnnotation(iterator.next());
      }

      private ResolvedAnnotation toResolvedAnnotation(Annotation annotation) {
         if (ResolvedAnnotation.class.isAssignableFrom(annotation.getClass())) {
            return (ResolvedAnnotation) annotation;
         }
         return new BaseResolvedAnnotation(annotation, fieldValue);
      }

      @Override
      public boolean hasPrevious() {
         return iterator.hasPrevious();
      }

      @Override
      public ResolvedAnnotation previous() {
         return toResolvedAnnotation(iterator.previous());
      }

      @Override
      public int nextIndex() {
         return iterator.nextIndex();
      }

      @Override
      public int previousIndex() {
         return iterator.previousIndex();
      }

      @Override
      public void set(ResolvedAnnotation resolvedAnnotation) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void add(ResolvedAnnotation resolvedAnnotation) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public String toString() {
      return "BaseAnnotatedField{" +
            "annotations=" + annotations +
            ", value='" + value + '\'' +
            '}';
   }
}
