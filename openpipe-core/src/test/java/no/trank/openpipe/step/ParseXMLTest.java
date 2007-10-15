package no.trank.openpipe.step;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class ParseXMLTest extends TestCase {
   private ParseXML xml;

   public void testParse() throws Exception {
      final Document doc = new Document();
//      doc.setFieldValue("xml", FileIO.readFile("/home/espen/projects/sandbox/pipeline-parent/pom.xml"));
      doc.setFieldValue("xml", "<project><test abc=\"attrib\">dillbert</test></project>");
      xml.setFieldName("xml");
      final Set<String> ignoredTags = new HashSet<String>(Arrays.asList("modelVersion", "name", "id"));
      xml.setIgnoredTags(ignoredTags);
      final HashMap<String, String> tagToFieldName = new HashMap<String, String>();
      tagToFieldName.put("artifactId", "depMod");
      xml.setTagToFieldName(tagToFieldName);
      xml.execute(doc);
      doc.removeField("xml");
      new Debug().execute(doc);
//      for (String tag : ignoredTags) {
//         assertTrue(doc.getFieldValues(tag).isEmpty());
//      }
//      for (Map.Entry<String, String> e : tagToFieldName.entrySet()) {
//         assertTrue(doc.getFieldValues(e.getKey()).isEmpty());
//         assertFalse(doc.getFieldValues(e.getValue()).isEmpty());
//      }
   }

   @Override
   protected void setUp() throws PipelineException {
      xml = new ParseXML();
      xml.prepare();
      
   }
}
 