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
package no.trank.openpipe.lemmatizer.parser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import it.unimi.dsi.io.FastBufferedReader;
import it.unimi.dsi.io.LineIterator;
import it.unimi.dsi.lang.MutableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.lemmatizer.model.LemmaSuffix;
import no.trank.openpipe.lemmatizer.model.LemmatizeModel;

/**
 * @version $Revision$
 */
public class TextParser implements Parser {
   private static final Logger log = LoggerFactory.getLogger(TextParser.class);

   @Override
   public void parse(Reader in, LemmatizeModel model) throws IOException {
      try {
         final LineIterator lineIt = new LineIterator(new FastBufferedReader(in));
         while (lineIt.hasNext()) {
            final MutableString line = lineIt.next().trim();
            if (line.length() > 0 && Character.isLetterOrDigit(line.charAt(0))) {
               final int tEndIdx = line.indexOf('\t');
               if (tEndIdx > 0) {
                  final CharSequence term = line.subSequence(0, tEndIdx);
                  try {
                     model.add(term, parseSuffixes(line, tEndIdx + 1));
                  } catch (Exception e) {
                     log.error("Trouble with line '" + line + '\'', e);
                  }
               }
            }
         }
      } finally {
         try {
            in.close();
         } catch (IOException e) {
            // Ignoring
         }
      }
   }

   public static List<LemmaSuffix> parseSuffixes(CharSequence line, int idx) {
      final int len = line.length();
      final List<LemmaSuffix> suffixes = new ArrayList<LemmaSuffix>();
      while (idx < len) {
         char c = line.charAt(idx++);
         int cut = c - '0';
         while (idx < len && isDigit(c = line.charAt(idx++))) {
            cut += cut * 10 + c - '0';
         }
         final int sIdx = isDigit(c) ? idx : idx - 1;
         while (idx < len && c != '\t') {
            c = line.charAt(idx++);
         }
         suffixes.add(new LemmaSuffix(cut, line.subSequence(sIdx, c == '\t' ? idx - 1 : idx)));
      }
      return suffixes;
   }

   private static boolean isDigit(final char c) {
      return c >= '0' && c <= '9';
   }

   @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
   public static void main(String[] args) throws IOException {
      if (args.length < 2) {
         System.err.println("Uasge: TextParser <input> <output>");
         System.exit(-1);
      }
      final LemmatizeModel model = new LemmatizeModel();
      new TextParser().parse(createReader(args[0]), model);
      model.log();
      final FileOutputStream fout = new FileOutputStream(args[1]);
      final OutputStream out;
      if (isGzip(args[1])) {
         out = new GZIPOutputStream(fout);
      } else {
         out = fout;
      }
      try {
         model.write(out);
      } finally {
         try {
            out.close();
         } catch (IOException e) {
            // Ignoring
         }
      }
   }

   private static Reader createReader(String fileName) throws IOException {
      if (isGzip(fileName)) {
         return new InputStreamReader(new GZIPInputStream(new FileInputStream(fileName)));
      }
      return new FileReader(fileName);
   }

   private static boolean isGzip(String fileName) {
      return fileName.endsWith(".gz");
   }
}
