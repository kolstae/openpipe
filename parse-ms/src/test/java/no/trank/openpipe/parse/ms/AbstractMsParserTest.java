package no.trank.openpipe.parse.ms;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserResult;

/**
 * @version $Revision$
 */
public abstract class AbstractMsParserTest extends TestCase {
   protected ParserResult result;
   
   protected abstract Class<? extends Parser> getParserClass();
   protected abstract String getFileName();
   
   protected void setUp() throws Exception {
      final Parser parser = getParserClass().newInstance();
      result = parser.parse(new TestData(getFileName()));
   }
   
   private static class TestData implements ParseData {
      private final String fileName;
      
      TestData(String fileName) {
         this.fileName = fileName;
      }
      
      public InputStream getInputStream() throws IOException {
         final InputStream in = getClass().getResourceAsStream("/" + fileName);
         assertNotNull(in);
         return in;
      }

      public int getLength() {
         return 0;
      }

      public boolean includeProperties() {
         return true;
      }

      public String getFileName() {
         return fileName;
      }
   }
}