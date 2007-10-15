package no.trank.openpipe.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @version $Revision$
 */
public class Iterators {
   private static final ListIterator EMPTY_ITERATOR = new ListIterator() {
      public boolean hasNext() {
         return false;
      }

      public Object next() {
         return null;
      }

      public boolean hasPrevious() {
         return false;
      }

      public Object previous() {
         return null;
      }

      public int nextIndex() {
         return 0;
      }

      public int previousIndex() {
         return -1;
      }

      public void remove() {
      }

      public void set(Object o) {
      }

      public void add(Object o) {
      }
   };

   public static <T> Iterator<T> iteratorForIterables(List<? extends Iterable<T>> lists) {
      final List<Iterator<T>> list = new ArrayList<Iterator<T>>(lists.size());
      for (Iterable<T> l : lists) {
         list.add(l.iterator());
      }
      return iterator(list);
   }

   public static <T> Iterator<T> iterator(Iterator<T> ... it) {
      return iterator(Arrays.asList(it));
   }

   public static <T> Iterator<T> iterator(List<? extends Iterator<T>> iterators) {
      return new MultiIterator<T>(iterators.iterator());
   }

   @SuppressWarnings({"unchecked"})
   public static <T> ListIterator<T> emptyIterator() {
      return (ListIterator<T>) EMPTY_ITERATOR;
   }

   @SuppressWarnings({"unchecked"})
   public static <T> Iterator<T> notNullIterator(Iterable<T> it) {
      return it == null ? EMPTY_ITERATOR : it.iterator();
   }

   private Iterators() {
      // Private
   }

   private static class MultiIterator<T> implements Iterator<T> {
      private final Iterator<? extends Iterator<T>> iterator;
      private Iterator<T> i = emptyIterator();

      private MultiIterator(Iterator<? extends Iterator<T>> it) {
         iterator = it;
         if (iterator.hasNext()) {
            i = iterator.next();
         }
      }

      public boolean hasNext() {
         boolean more = i.hasNext();
         while (!more && iterator.hasNext()) {
            i = iterator.next();
            more = i.hasNext();
         }
         return more;
      }

      public T next() {
         return i.next();
      }

      public void remove() {
         i.remove();
      }
   }
}
