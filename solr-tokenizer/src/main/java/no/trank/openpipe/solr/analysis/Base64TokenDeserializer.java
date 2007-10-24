package no.trank.openpipe.solr.analysis;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

import no.trank.openpipe.solr.analysis.io.Base64InputStream;
import static no.trank.openpipe.solr.util.IOUtil.readNibble;
import static no.trank.openpipe.solr.util.IOUtil.readUTF;

/**
 * @version $Revision$
 */
public class Base64TokenDeserializer extends Tokenizer {
   private DataInputStream in;

   public Base64TokenDeserializer(Reader input) {
      in = new DataInputStream(new Base64InputStream(input));
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
}
