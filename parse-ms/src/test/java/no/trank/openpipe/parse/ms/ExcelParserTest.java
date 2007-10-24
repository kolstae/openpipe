package no.trank.openpipe.parse.ms;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.ParserResult;

/**
 * @version $Revision$
 */
public class ExcelParserTest extends TestCase {
   public void testParseXLS() throws Exception {
      final ExcelParser parser = new ExcelParser();
      final ParserResult result = parser.parse(new TestData("test.xls"));
      final String text = result.getText();
      
      assertNotNull(text);
      assertEquals(true, text.equals("Cell text 4.0\n\nSheet 2 text\n"));
      assertEquals("Frode Johannesen", result.getProperties().get("author"));
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
