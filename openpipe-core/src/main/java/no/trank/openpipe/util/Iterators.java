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
      @Override
      public boolean hasNext() {
         return false;
      }

      @Override
      public Object next() {
         return null;
      }

      @Override
      public boolean hasPrevious() {
         return false;
      }

      @Override
      public Object previous() {
         return null;
      }

      @Override
      public int nextIndex() {
         return 0;
      }

      @Override
      public int previousIndex() {
         return -1;
      }

      @Override
      public void remove() {
      }

      @Override
      public void set(Object o) {
      }

      @Override
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

      @Override
      public boolean hasNext() {
         boolean more = i.hasNext();
         while (!more && iterator.hasNext()) {
            i = iterator.next();
            more = i.hasNext();
         }
         return more;
      }

      @Override
      public T next() {
         return i.next();
      }

      @Override
      public void remove() {
         i.remove();
      }
   }
}
