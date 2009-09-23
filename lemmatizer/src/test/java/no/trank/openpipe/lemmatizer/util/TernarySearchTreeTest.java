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
package no.trank.openpipe.lemmatizer.util;

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @version $Revision$
 */
public class TernarySearchTreeTest extends TestCase {

   public void testTree() throws Exception {
      final TestTreeValueFactory factory = new TestTreeValueFactory();
      final TernarySearchTree<TestTreeValue> tree = new TernarySearchTree<TestTreeValue>(factory);
      final HashMap<String, TestTreeValue> map = new HashMap<String, TestTreeValue>();
      put(tree, map, "test1", factory);
      put(tree, map, "test2", factory);
      put(tree, map, "test3", factory);
      put(tree, map, "test4", factory);
      put(tree, map, "test5", factory);
      put(tree, map, "abcdef", factory);
      put(tree, map, "abc", factory);
      put(tree, map, "abcd", factory);
      put(tree, map, "abcde", factory);
      checkValues(tree, map);
      final File file = File.createTempFile("tree", ".bin");
      file.deleteOnExit();
      final FileOutputStream out = new FileOutputStream(file);
      try {
         tree.write(out);
      } finally {
         out.close();
      }
      final FileInputStream in = new FileInputStream(file);
      try {
         final TernarySearchTree<TestTreeValue> t = new TernarySearchTree<TestTreeValue>(new TestTreeValueFactory());
         t.read(in);
         checkValues(t, map);
      } finally {
         in.close();
      }
   }

   private static void checkValues(TernarySearchTree<TestTreeValue> tree, HashMap<String, TestTreeValue> map) {
      assertEquals(map.size(), tree.size());
      for (Map.Entry<String, TestTreeValue> entry : map.entrySet()) {
         assertEquals(entry.getValue(), tree.get(entry.getKey()));
      }
   }

   private static void put(TernarySearchTree<TestTreeValue> tree, HashMap<String, TestTreeValue> map, String key,
                           TestTreeValueFactory factory) {
      final TestTreeValue value = factory.newValue();
      map.put(key, value);
      tree.put(key, value);
      assertEquals(value, tree.get(key));
   }

   private static class TestTreeValue implements TreeValue {
      private int id;

      private TestTreeValue(int id) {
         this.id = id;
      }

      @Override
      public void write(OutputBitStream out) throws IOException {
         out.writeNibble(id);
      }

      @Override
      public void read(InputBitStream out) throws IOException {
         id = out.readNibble();
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         }
         if (o == null || getClass() != o.getClass()) {
            return false;
         }

         return id == ((TestTreeValue) o).id;
      }

      @Override
      public int hashCode() {
         return id;
      }
   }

   private static class TestTreeValueFactory implements TreeValueFactory<TestTreeValue> {
      private int nextId;

      @Override
      public TestTreeValue newValue() {
         return new TestTreeValue(nextId++);
      }

      @Override
      public void writeHeader(OutputBitStream out) throws IOException {
         out.writeNibble(nextId);
      }

      @Override
      public void readHeader(InputBitStream in) throws IOException {
         nextId = in.readNibble();
      }

      @Override
      public long getSerialVersionUID() {
         return 313L;
      }
   }
}