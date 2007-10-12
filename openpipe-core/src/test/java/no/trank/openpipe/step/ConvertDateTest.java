package no.trank.openpipe.step;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision: 874 $
 */
public class ConvertDateTest extends TestCase {
   public void testExecute() throws Exception {
      ConvertDate convertDate = new ConvertDate();
      
      Map<String, String> fieldNameMap = new HashMap<String, String>();
      fieldNameMap.put("in", "out");
      convertDate.setFieldNameMap(fieldNameMap);
      
      LinkedHashMap<String, String> patternMap = new LinkedHashMap<String, String>();
      patternMap.put("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'");
      convertDate.setPatternMap(patternMap);
      convertDate.prepare();
      
      Document doc = new Document();
      doc.setFieldValue("in", "2009-02-03 12:13:14");
      
      convertDate.execute(doc);
      
      assertEquals("2009-02-03T12:13:14Z", doc.getFieldValue("out"));
   }
}