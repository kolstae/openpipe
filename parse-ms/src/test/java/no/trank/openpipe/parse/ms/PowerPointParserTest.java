package no.trank.openpipe.parse.ms;

import no.trank.openpipe.parse.api.Parser;


/**
 * @version $Revision$
 */
public class PowerPointParserTest extends AbstractMsParserTest {
   public void testParsePPT() throws Exception {
      final String text = result.getText();
      
      assertNotNull(text);
      assertEquals(true, text.contains("Slide 1"));
      assertEquals(true, text.contains("Slide 2"));
      assertEquals(true, text.contains("Text on slide 2"));
      assertEquals("Frode Johannesen", result.getProperties().get("author"));
   }
   
   @Override
   protected Class<? extends Parser> getParserClass() {
      return PowerPointParser.class;
   }
   
   @Override
   protected String getFileName() {
      return "test.ppt";
   }
}