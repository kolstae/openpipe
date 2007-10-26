package no.trank.openpipe.parse.ms;

import no.trank.openpipe.parse.api.Parser;


/**
 * @version $Revision$
 */
public class ExcelParserTest extends AbstractMsParserTest {
   public void testParseXLS() throws Exception {
      final String text = result.getText();
      
      assertNotNull(text);
      assertEquals(true, text.equals("Cell text 4.0\n\nSheet 2 text\n"));
      assertEquals("Frode Johannesen", result.getProperties().get("author"));
   }
   
   protected Class<? extends Parser> getParserClass() {
      return ExcelParser.class;
   }
   
   protected String getFileName() {
      return "test.xls";
   }
}