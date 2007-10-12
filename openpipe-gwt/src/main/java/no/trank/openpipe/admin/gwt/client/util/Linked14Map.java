package no.trank.openpipe.admin.gwt.client.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Inefficient, temporary LinkedHashMap replacement. GWT should support LinkedHashMap in near future.
 * 
 * Keeps an ordered collection of keys and map entries.
 * 
 * @version $Revision: 874 $
 */
public class Linked14Map implements Map, IsSerializable {
   private List keys = new ArrayList();
   private List values = new ArrayList();
   
   public Linked14Map() {
   }
   
   public void clear() {
      keys.clear();
      values.clear();
   }

   public boolean containsKey(Object key) {
      return keys.contains(key);
   }

   public boolean containsValue(Object value) {
      return values.contains(value);
   }

   public Set entrySet() {
      return new AbstractSet() {
         public Iterator iterator() {
            return new Iterator() {
               int nextIndex = 0;
               Entry entry = new Entry();
               
               public boolean hasNext() {
                  return nextIndex < keys.size();
               }

               public Object next() {
                  entry.index = nextIndex++;
                  return entry;
               }

               public void remove() {
                  keys.remove(entry.index);
                  values.remove(entry.index);
                  --nextIndex;
               }
            };
         }
         public int size() {
            return keys.size();
         }
         public boolean contains(Object o) {
            return o instanceof Entry && keys.contains(((Entry)o).getKey());
         }
         public boolean remove(Object o) {
            return o instanceof Entry && Linked14Map.this.remove(((Entry)o).getKey()) != null;
         }
         public void clear() {
            Linked14Map.this.clear();
         }
      };
   }

   public Object get(Object key) {
      int index = keys.indexOf(key);
      return index >= 0 ? values.get(index) : null;
   }

   public boolean isEmpty() {
      return keys.isEmpty();
   }

   public Set keySet() {
      return new AbstractSet() {
         public Iterator iterator() {
            return keys.iterator();
         }
         public int size() {
            return keys.size();
         }
         public boolean contains(Object o) {
            return containsKey(o);
         }
         public boolean remove(Object o) {
            return Linked14Map.this.remove(o) != null;
         }
         public void clear() {
           Linked14Map.this.clear();
         }
      };
   }

   public Object put(Object key, Object value) {
      int index = keys.indexOf(key);
      Object ret = null;
      if(index != -1) {
         ret = keys.get(index);
         keys.set(index, key);
         values.set(index, value);
      }
      else {
         keys.add(key);
         values.add(value);
      }
      
      return ret;
   }

   public void putAll(Map m) {
      for(Iterator it = m.entrySet().iterator(); it.hasNext(); ) {
         Entry entry = (Entry)it.next();
         put(entry.getKey(), entry.getValue());
      }
   }

   public Object remove(Object key) {
      int index = keys.indexOf(key);
      if(index != -1) {
         keys.remove(index);
         values.remove(index);
         return key;
      }
      
      return null;
   }

   public int size() {
      return keys.size();
   }

   public Collection values() {
      return values;
   }
   
   
   private class Entry implements Map.Entry {
      int index;

      public Object getKey() {
         return keys.get(index);
      }

      public Object getValue() {
         return values.get(index);
      }

      public Object setValue(Object value) {
         Object ret = values.get(index);
         values.set(index, value);
         return ret;
      }
   }
}