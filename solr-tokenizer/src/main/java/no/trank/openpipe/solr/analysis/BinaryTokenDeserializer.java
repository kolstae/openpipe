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
package no.trank.openpipe.solr.analysis;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

import no.trank.openpipe.solr.analysis.io.Base64InputStream;
import static no.trank.openpipe.solr.util.IOUtil.readNibble;
import static no.trank.openpipe.solr.util.IOUtil.readUTF;

/**
 * This class reads tokens on the binary form:
 * <pre>
 *   token.startOffset = IOUtil.readNibble(in);
 *   token.endOffset = token.startOffset + IOUtil.readNibble(in);
 *   token.positionIncrement = IOUtil.readNibble(in);
 *   token.termText = IOUtil.readUTF(in);
 *   token.type = IOUtil.readUTF(in);
 * </pre>
 * 
 * @see no.trank.openpipe.solr.util.IOUtil#readNibble(InputStream)
 * @see no.trank.openpipe.solr.util.IOUtil#readUTF(InputStream) 
 * 
 * @version $Revision$
 */
public class BinaryTokenDeserializer extends Tokenizer {
   private InputStream in;

   public BinaryTokenDeserializer(Reader input) {
      if (input instanceof DummyReader) {
         in = ((DummyReader) input).getInputStream();
      } else {
         in = new Base64InputStream(input);
      }
   }

   public BinaryTokenDeserializer(InputStream in) {
      this.in = in;
   }

   @Override
   public Token next() throws IOException {
      try {
         final int start = readNibble(in);
         final int end = readNibble(in) + start;
         final int posIncr = readNibble(in);
         if (start < 0 || end < 0 || posIncr < 0) {
            return null;
         }
         final String text = readUTF(in);
         final String type = readUTF(in);
         final Token token = new Token(text, start, end, type);
         token.setPositionIncrement(posIncr);
         return token;
      } catch (EOFException e) {
         // Ignoring
      }
      return null;
   }

   @Override
   public void close() throws IOException {
      if (in != null) {
         in.close();
         in = null;
      }
   }

   /**
    * Work around for <tt>pre 1.2</tt> Solr. Has not been tested, but should do the trick to solr version &lt; 
    * <tt>1.2</tt>.
    */
   public static Reader createDummyReader(InputStream in) {
      return new DummyReader(in);
   }

   private static class DummyReader extends Reader {
      private final InputStream inputStream;

      public DummyReader(InputStream inputStream) {
         this.inputStream = inputStream;
      }

      public InputStream getInputStream() {
         return inputStream;
      }

      @Override
      public int read(char cbuf[], int off, int len) throws IOException {
         throw new UnsupportedOperationException();
      }

      @Override
      public void close() throws IOException {
      }
   }
}
