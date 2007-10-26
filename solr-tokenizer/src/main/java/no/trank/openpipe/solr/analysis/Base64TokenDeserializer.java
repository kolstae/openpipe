package no.trank.openpipe.solr.analysis;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;

import static no.trank.openpipe.solr.util.IOUtil.readNibble;
import static no.trank.openpipe.solr.util.IOUtil.readUTF;

/**
 * @version $Revision$
 */
public class Base64TokenDeserializer extends Tokenizer {
   private InputStream in;
   private Tokenizer tokenizer;

   public Base64TokenDeserializer(Reader input) {
      if (input instanceof DummyReader) {
         in = ((DummyReader) input).getInputStream();
      } else {
         tokenizer = new WhitespaceTokenizer(input);
      }
   }

   public Base64TokenDeserializer(InputStream in) {
      this.in = in;
   }

   @Override
   public Token next() throws IOException {
      if (in == null) {
         return tokenizer.next();
      }
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
      if (tokenizer != null) {
         try {
            tokenizer.close();
         } finally {
            tokenizer = null;
         }
      }
   }


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
