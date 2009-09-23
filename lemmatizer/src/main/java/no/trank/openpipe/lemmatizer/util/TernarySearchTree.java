package no.trank.openpipe.lemmatizer.util;

/*		 
* MG4J: Managing Gigabytes for Java
*
* Copyright (C) 2005-2007 Sebastiano Vigna 
*
*  This library is free software; you can redistribute it and/or modify it
*  under the terms of the GNU Lesser General Public License as published by the Free
*  Software Foundation; either version 2.1 of the License, or (at your option)
*  any later version.
*
*  This library is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
*  for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program; if not, write to the Free Software
*  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*
*/

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import it.unimi.dsi.lang.MutableString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class TernarySearchTree<V extends TreeValue> {
   private static final byte VERSION = (byte) 1;
   private static final byte[] HEADER = new byte[]{(byte) 'o', (byte) '|', VERSION};
   /**
    * The root of the tree.
    */
   private Node<V> root;
   /**
    * The number of nodes in the tree.
    */
   private int size;
   private final TreeValueFactory<V> factory;
   private static final int LEN_ZK = 1;
   private static final int CHAR_ZK = 3;

   /**
    * Creates a new empty ternary search tree.
    */
   public TernarySearchTree(TreeValueFactory<V> factory) {
      if (factory == null) {
         throw new NullPointerException("TreeValueFactory cannot be null");
      }
      this.factory = factory;
   }

   /**
    * Creates a new empty ternary search tree and populates it with a given collection of character sequences.
    *
    * @param c a collection of character sequences.
    */
   public TernarySearchTree(final Iterable<TreeEntry<V>> c, TreeValueFactory<V> factory) {
      this(factory);
      for (final TreeEntry<V> entry : c) {
         put(entry.getKey(), entry.getValue());
      }
   }


   public V get(final CharSequence s) {
      final int l = s.length();

      Node<V> e = root;
      int offset = 0;

      while (e != null) {
         final char[] path = e.path;
         int i = 0;
         for (; i < path.length - 1; i++) {
            if (offset + i == l || s.charAt(offset + i) != path[i]) {
               return null;
            }
         }

         offset += i;
         if (offset == l) {
            return null;
         }

         final char c = s.charAt(offset);
         if (c < e.path[i]) {
            e = e.left;
         } else if (c > e.path[i]) {
            e = e.right;
         } else {
            offset++;
            if (offset == l) {
               return e.value;
            }
            e = e.middle;
         }
      }

      return null;
   }

   public boolean contains(CharSequence s) {
      return get(s) != null;
   }

   /**
    * True if the last {@link #add(CharSequence)} modified the tree.
    */
   private boolean modified;

   public boolean put(final CharSequence s, final V value) {
      modified = false;
      root = addRec(s, 0, s.length(), root, value);
      return modified;
   }

   /**
    * Inserts the given character sequence, starting at the given position, in the given subtree.
    *
    * @param s      the character sequence containing the characters to be inserted.
    * @param offset the first character to be inserted.
    * @param length the number of characters to be inserted.
    * @param e      the subtree in which the characters should be inserted, or <code>null</code> if
    *               a new node should be created.
    * @return the new node at the top of the subtree.
    */

   private Node<V> addRec(final CharSequence s, final int offset, final int length, final Node<V> e, final V value) {

      if (e == null) {
         // We create a new node containing all the characters and return it.
         modified = true;
         size++;
         return new Node<V>(s, offset, length, value);
      }

      /* We start scanning the path contained in the current node, up to
         * the last character excluded. If we find a mismatch, or if we exhaust our
         * characters, we must fork this node. */

      int i;
      Node<V> n = null;
      final char[] path = e.path;

      for (i = 0; i < path.length - 1; i++) {
         final char c = s.charAt(offset + i);

         if (c < path[i]) {
            /* We fork on the left, keeping just the first i + 1 characters (this is necessary
                 * as at least one character must be present in every node). The new
                 * node will cover one word more than e.
                 */
            n = new Node<V>(path, 0, i + 1, null);

            n.middle = e;
            e.removePathPrefix(i + 1);

            n.left = addRec(s, offset + i, length - i, null, value);
            break;
         } else if (c > path[i]) {
            // As before, but on the right.
            n = new Node<V>(path, 0, i + 1, null);

            n.middle = e;
            e.removePathPrefix(i + 1);

            n.right = addRec(s, offset + i, length - i, null, value);
            break;
         } else {
            if (i == length - 1) {
               /* We exhausted the character sequence. We fork in the middle,
                     * keeping length characters and marking the new node as
                     * containing one work. Again, the new code will cover one word
                     * more than e. */
               n = new Node<V>(s, offset, length, value);
               n.middle = e;
               e.removePathPrefix(length);
               size++;
               modified = true;
               break;
            }
         }
      }

      if (i < path.length - 1) {
         return n;
      }

      /* We are positioned on the last character of the path. In this case our
         * behaviour is different, as if we must fork we must not perform any
         * splitting. Moreover, if we exhaust the characters we either found
         * the new sequence in the tree, or we just have to mark the node. */

      final char c = s.charAt(offset + i);

      if (c < path[i]) {
         /** We fork on the left. The number of words under this node will
          * increase only if the structure is modified. */
         e.left = addRec(s, offset + i, length - i, e.left, value);
      } else if (c > path[i]) {
         e.right = addRec(s, offset + i, length - i, e.right, value);
      } else {
         if (i == length - 1) {
            // This is the node.
            if (modified = e.value != null) {
               size++;
            }
            e.value = value;
         } else {
            // We add a node in the middle, completing the sequence.
            e.middle = addRec(s, offset + i + 1, length - i - 1, e.middle, value);
         }
      }

      return e;
   }

   public int size() {
      return size;
   }

   public void read(InputStream in) throws IOException {
      readHeader(in);
      final InputBitStream inB = new InputBitStream(in);
      final long serialVersionUID = inB.readLongNibble();
      if (serialVersionUID != factory.getSerialVersionUID()) {
         throw new IOException("serialVersionUID missmatch, read " + serialVersionUID + " exptected " +
               factory.getSerialVersionUID());
      }
      size = inB.readNibble();
      factory.readHeader(inB);
      root = readNode(inB);
   }

   private Node<V> readNode(InputBitStream in) throws IOException {
      final int len = in.readZeta(LEN_ZK);
      if (len > 0) {
         final char[] path = new char[len];
         for (int i = 0; i < len; i++) {
            path[i] = (char) (in.readZeta(CHAR_ZK) + '0');
         }
         final V value;
         if (in.readBit() == 1) {
            value = factory.newValue();
            value.read(in);
         } else {
            value = null;
         }
         final Node<V> node = new Node<V>(path, value);
         node.left = readNode(in);
         node.middle = readNode(in);
         node.right = readNode(in);
         return node;
      }
      return null;
   }

   private static void readHeader(InputStream in) throws IOException {
      final byte[] buf = new byte[HEADER.length];
      final int len = in.read(buf);
      if (len != buf.length) {
         throw new IOException("Could not read header from stream, got " + len + " bytes expected " + HEADER.length);
      } else if (!Arrays.equals(HEADER, buf)) {
         throw new IOException("Could not read header from stream, got " + Arrays.toString(buf) + " expected " +
               Arrays.toString(HEADER));
      }
   }

   public void write(OutputStream out) throws IOException {
      out.write(HEADER);
      final OutputBitStream outB = new OutputBitStream(out);
      try {
         outB.writeLongNibble(factory.getSerialVersionUID());
         outB.writeNibble(size);
         factory.writeHeader(outB);
         writeNode(root, outB);
      } finally {
         outB.flush();
      }
   }

   private void writeNode(final Node<V> node, final OutputBitStream out) throws IOException {
      if (node == null) {
         out.writeZeta(0, LEN_ZK);
      } else {
         final char[] path = node.path;
         final int len = path.length;
         out.writeZeta(len, LEN_ZK);
         for (int i = 0; i < len; i++) {
            out.writeZeta(path[i] - '0', CHAR_ZK);
         }
         final boolean hasValue = node.value != null;
         out.writeBit(hasValue);
         if (hasValue) {
            node.value.write(out);
         }
         writeNode(node.left, out);
         writeNode(node.middle, out);
         writeNode(node.right, out);
      }
   }

   /**
    * A node of the tree.
    */
   private static final class Node<V extends TreeValue> {
      /**
       * A pointer to the left subtree.
       */
      private Node<V> left;
      /**
       * A pointer to the middle subtree.
       */
      private Node<V> middle;
      /**
       * A pointer to the right subtree.
       */
      private Node<V> right;
      /**
       * The nonempty path compressed at this node.
       */
      private char[] path;
      /**
       * Whether this node represents a word.
       */
      private V value;

      /**
       * Creates a new node containing a path specified by a character-sequence fragment.
       *
       * @param s        a character sequence contaning the path of the node.
       * @param offset   the starting character of the path.
       * @param length   the length of the path.
       * @param value    the value of this node.
       */
      public Node(final CharSequence s, final int offset, final int length, final V value) {
         this.value = value;
         path = new char[length];
         MutableString.getChars(s, offset, offset + length, path, 0);
      }

      /**
       * Creates a new node containing a path specified by a character-array fragment.
       *
       * @param a        a character array contaning the path of the node.
       * @param offset   the starting character of the path.
       * @param length   the length of the path.
       * @param value    the value of this node.
       */
      public Node(final char[] a, final int offset, final int length, final V value) {
         this.value = value;
         path = new char[length];
         System.arraycopy(a, offset, path, 0, length);
      }

      /**
       * Creates a new node containing a path specified by a character-array fragment.
       *
       * @param path     a character array contaning the path of the node.
       * @param value    the value of this node.
       */
      public Node(char[] path, V value) {
         this.path = path;
         this.value = value;
      }

      /**
       * Removes a prefix from the path of this node.
       *
       * @param length the length of the prefix to be removed
       */
      public void removePathPrefix(final int length) {
         final char[] a = new char[path.length - length];
         System.arraycopy(path, length, a, 0, a.length);
         path = a;
      }
   }
}