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
package no.trank.openpipe.lemmatizer.model;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import it.unimi.dsi.lang.MutableString;

import java.io.IOException;
import java.util.*;

/**
 * @version $Revision$
 */
public class LemmaDeSerializer {
   private static final int INT_ZK = 1;
   private static final int CHAR_ZK = 2;
   private static final char CHAR_OFFSET = 'a';

   protected static byte[] createLemmasData(Iterable<LemmaSuffix> suffixes) throws IOException {
      final IntList cuts = new IntArrayList();
      final IntList lens = new IntArrayList();
      final List<CharSequence> suffs = new ArrayList<CharSequence>();
      for (final LemmaSuffix suf : suffixes) {
         cuts.add(suf.getCut());
         final CharSequence suffix = suf.getSuffix();
         lens.add(suffix.length());
         suffs.add(suffix);
      }
      final byte[] buf = new byte[4096];
      final OutputBitStream out = new OutputBitStream(buf);
      out.writeZeta(cuts.size() - 1, INT_ZK);
      writeInts(out, lens);
      for (CharSequence suffix : suffs) {
         writeSuffix(out, suffix);
      }
      writeInts(out, cuts);
      out.flush();
      return Arrays.copyOfRange(buf, 0, (int) (out.writtenBits() / Byte.SIZE));
   }

   private static void writeSuffix(OutputBitStream out, CharSequence suffix) throws IOException {
      for (int i = 0; i < suffix.length(); i++) {
         out.writeZeta(suffix.charAt(i) - CHAR_OFFSET, CHAR_ZK);
      }
   }

   private static void writeInts(OutputBitStream out, IntList lens) throws IOException {
      for (IntIterator it = lens.iterator(); it.hasNext();) {
         out.writeZeta(it.nextInt(), INT_ZK);
      }
   }

   public static Iterator<String> createIterator(CharSequence lemma, byte[] data) throws IOException {
      return new LemmaIterator(lemma, data);
   }

   private static class LemmaIterator implements Iterator<String> {
      private final CharSequence lemma;
      private final int count;
      private final int[] cuts;
      private final char[][] suffixes;
      private int idx = 0;

      public LemmaIterator(CharSequence lemma, byte[] data) throws IOException {
         this.lemma = lemma;
         final InputBitStream in = new InputBitStream(data);
         count = in.readZeta(INT_ZK) + 1;
         cuts = new int[count];
         in.readZetas(INT_ZK, cuts, count);
         suffixes = new char[count][];
         for (int j = 0; j < cuts.length; j++) {
            int len = cuts[j];
            final char[] suf = new char[len];
            suffixes[j] = suf;
            for (int i = 0; i < len; i++) {
               suf[i] = (char) (in.readZeta(CHAR_ZK) + CHAR_OFFSET);
            }
         }
         in.readZetas(INT_ZK, cuts, count);
      }

      @Override
      public boolean hasNext() {
         return idx < count;
      }

      @Override
      public String next() {
         if (!hasNext()) {
            throw new NoSuchElementException();
         }
         final MutableString l = new MutableString(lemma.length() - cuts[idx] + suffixes[idx].length);
         l.append(lemma, 0, lemma.length() - cuts[idx]);
         l.append(suffixes[idx++]);
         return l.toString();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
