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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.lemmatizer.util.TernarySearchTree;
import no.trank.openpipe.lemmatizer.util.TreeValue;
import no.trank.openpipe.lemmatizer.util.TreeValueFactory;
import no.trank.openpipe.util.Iterators;
import no.trank.openpipe.util.log.DefaultTimedLogger;
import no.trank.openpipe.util.log.TimedLogger;

/**
 * @version $Revision$
 */
public class LemmatizeModel {
   private static final Logger log = LoggerFactory.getLogger(LemmatizeModel.class);
   private final TimedLogger tlA = createTimedLogger("Added %1$d (%3$d) lemmas at %2$.2f (%4$.2f) micros/lemma");
   private final TimedLogger tlC = createTimedLogger("Created %1$d (%3$d) kb at %2$.2f (%4$.2f) kb/sec",
         TimeUnit.SECONDS, DefaultTimedLogger.Calculator.UNIT_PER_TIME);
   private final TimedLogger tlG = createTimedLogger("Got %1$d (%3$d) lemmas at %2$.2f (%4$.2f) micros/lemma");
   private final TernarySearchTree<Lemmas> lemmas;
   private final LemmasFactory factory;
   private int rest = 0;

   private static TimedLogger createTimedLogger(String format) {
      return createTimedLogger(format, TimeUnit.MICROSECONDS, DefaultTimedLogger.Calculator.TIME_PER_UNIT);
   }

   private static TimedLogger createTimedLogger(String format, TimeUnit unit, DefaultTimedLogger.Calculator calculator) {
      final DefaultTimedLogger logger = new DefaultTimedLogger(log, format, unit, calculator);
      logger.setLogPeriodInSeconds(60);
      return logger;
   }

   public LemmatizeModel() {
      factory = new LemmasFactory();
      lemmas = new TernarySearchTree<Lemmas>(factory);
   }

   public void add(CharSequence term, Iterable<LemmaSuffix> suffixes) {
      try {
         tlC.startTimer();
         final byte[] data = LemmaDeSerializer.createLemmasData(suffixes);
         final int len = data.length + rest;
         tlC.stopTimerAndIncrement(len / 1024);
         rest = len % 1024;
         tlA.startTimer();
         lemmas.put(term, factory.newValue(data));
         tlA.stopTimerAndIncrement();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public Iterator<String> get(CharSequence term) {
      tlG.startTimer();
      try {
         final Lemmas lemmas = this.lemmas.get(term);
         if (lemmas != null) {
            return lemmas.getLemmas(term);
         }
         return Iterators.emptyIterator();
      } finally {
         tlG.stopTimerAndIncrement();
      }
   }

   public void log() {
      tlA.log();
      tlC.log();
      tlG.log();
   }

   public void write(OutputStream out) throws IOException {
      lemmas.write(out);
   }

   public void read(InputStream in) throws IOException {
      lemmas.read(in);
      reset();
   }

   public void reset() {
      tlA.reset();
      tlC.reset();
      tlG.reset();
   }

   private static class LemmasFactory implements TreeValueFactory<Lemmas> {
      private int zetaK = 2;

      @Override
      public Lemmas newValue() {
         return new Lemmas(this);
      }

      public Lemmas newValue(byte[] data) {
         return new Lemmas(data, this);
      }

      @Override
      public void writeHeader(OutputBitStream out) throws IOException {
         out.writeNibble(zetaK);
      }

      @Override
      public void readHeader(InputBitStream in) throws IOException {
         zetaK = in.readNibble();
      }

      @Override
      public long getSerialVersionUID() {
         return 16092084964425728L;
      }

      public int getZetaK() {
         return zetaK;
      }
   }

   public static class Lemmas implements TreeValue {
      private final LemmasFactory factory;
      private byte[] data;

      private Lemmas(LemmasFactory factory) {
         this.factory = factory;
      }

      private Lemmas(byte[] data, LemmasFactory factory) {
         this.data = data;
         this.factory = factory;
      }

      public Iterator<String> getLemmas(final CharSequence lemma) {
         try {
            return LemmaDeSerializer.createIterator(lemma, data);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public void write(OutputBitStream out) throws IOException {
         out.writeZeta(data.length - 1, factory.getZetaK());
         out.write(data, data.length * Byte.SIZE);
      }

      @Override
      public void read(InputBitStream in) throws IOException {
         data = new byte[in.readZeta(factory.getZetaK()) + 1];
         in.read(data, data.length * Byte.SIZE);
      }
   }
}
