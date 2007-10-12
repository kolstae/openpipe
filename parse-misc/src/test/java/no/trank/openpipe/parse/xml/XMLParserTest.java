package no.trank.openpipe.parse.xml;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.ParserResult;

/**
 * @version $Revision: 874 $
 */
public class XMLParserTest extends TestCase {

   public void testParse() throws Exception {
      final XMLParser parser = new XMLParser();
      final ParserResult result = parser.parse(new TestData());
      final String text = result.getText();
      assertNotNull(text);
      assertTrue(text.indexOf("ignored") < 0);
      assertTrue(text.indexOf("function matchwo(a,b)") >= 0);
      assertTrue(text.indexOf('>') >= 0);
      assertEquals("UTF-8", result.getProperties().get("encoding"));
   }

   private static class TestData implements ParseData {
      public InputStream getInputStream() throws IOException {
         final InputStream in = getClass().getResourceAsStream("/xml/test.xml");
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
         return "dummy.xml";
      }
   }
}
