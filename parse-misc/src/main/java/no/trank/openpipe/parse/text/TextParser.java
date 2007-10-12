package no.trank.openpipe.parse.text;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

/**
 * @version $Revision: 874 $
 */
public class TextParser implements Parser, Closeable {
   private final TextDecoder decoder;

   public TextParser() {
      this(new TextDecoder());
   }

   public TextParser(TextDecoder decoder) {
      this.decoder = decoder;
   }

   public ParserResult parse(ParseData data) throws IOException, ParserException {
      final String text = decode(data);
      if (text == null) {
         throw new ParserException("Unable to decode data");
      }
      final ParserResultImpl result = new ParserResultImpl(text);
      if (data.includeProperties()) {
         result.setProperties(Collections.singletonMap("encoding", decoder.getEncoding()));
      }
      return result;
   }

   protected String decode(ParseData data) throws IOException {
      return decoder.decode(data);
   }

   public void close() throws IOException {
      decoder.close();
   }
}
