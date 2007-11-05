package no.trank.openpipe.parse.ms;

import no.trank.openpipe.parse.api.Parser;


/**
 * @version $Revision$
 */
public class WordParserTest extends AbstractMsParserTest {
   public void testParsePPT() throws Exception {
      final String text = result.getText();
      
      assertNotNull(text);
      assertEquals(true, text.contains("Some text Text in a table  HYPERLINK \"http://link.link.link/\" Link"));
      assertEquals("Frode Johannesen", result.getProperties().get("author"));
      assertEquals("3", result.getProperties().get("revNumber"));
   }
   
   @Override
   protected Class<? extends Parser> getParserClass() {
      return WordParser.class;
   }
   
   @Override
   protected String getFileName() {
      return "test.doc";
   }
}