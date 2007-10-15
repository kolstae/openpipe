package no.trank.openpipe.step;

import java.util.HashMap;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class ParseXMLXPathTest extends TestCase {
   private ParseXMLXPath xml;

   public void testParse() throws Exception {
      final Document doc = new Document();
//      doc.setFieldValue("xml", FileIO.readFile("/home/espen/projects/sandbox/pipeline-parent/pom.xml"));
      doc.setFieldValue("xml", "<project><test abc=\"attrib\">dillbert</test></project>");
      xml.setFieldName("xml");
      final HashMap<String, String> xpaths = new HashMap<String, String>();
      xpaths.put("/project//dependencies/*[groupId='no.trank.pipeline']", "groupId");
      xml.setXPathToFieldName(xpaths);
      xml.prepare();
      xml.execute(doc);
      doc.removeField("xml");
      new Debug().execute(doc);
//      for (Map.Entry<String, String> e : xpaths.entrySet()) {
//         assertTrue(doc.getFieldValues(e.getKey()).isEmpty());
//         assertFalse(doc.getFieldValues(e.getValue()).isEmpty());
//      }
   }

   @Override
   protected void setUp() throws PipelineException {
      xml = new ParseXMLXPath();      
   }
}