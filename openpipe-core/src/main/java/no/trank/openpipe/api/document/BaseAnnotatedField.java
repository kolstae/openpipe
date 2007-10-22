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

   public Set<String> getAnnotationTypes() {
      return Collections.unmodifiableSet(annotations.keySet());
   }

   public boolean add(String type, List<? extends Annotation> annotations) {
      final boolean res = !this.annotations.containsKey(type);
      if (res) {
         set(type, annotations);
      }
      return res;
   }

   public void set(String type, List<? extends Annotation> annotations) {
      this.annotations.put(type, Collections.unmodifiableList(annotations));
   }

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

      public boolean hasNext() {
         return iterator.hasNext();
      }

      public ResolvedAnnotation next() {
         return toResolvedAnnotation(iterator.next());
      }

      private ResolvedAnnotation toResolvedAnnotation(Annotation annotation) {
         if (ResolvedAnnotation.class.isAssignableFrom(annotation.getClass())) {
            return (ResolvedAnnotation) annotation;
         }
         return new BaseResolvedAnnotation(annotation, fieldValue);
      }

      public boolean hasPrevious() {
         return iterator.hasPrevious();
      }

      public ResolvedAnnotation previous() {
         return toResolvedAnnotation(iterator.previous());
      }

      public int nextIndex() {
         return iterator.nextIndex();
      }

      public int previousIndex() {
         return iterator.previousIndex();
      }

      public void set(ResolvedAnnotation resolvedAnnotation) {
         throw new UnsupportedOperationException();
      }

      public void add(ResolvedAnnotation resolvedAnnotation) {
         throw new UnsupportedOperationException();
      }

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
