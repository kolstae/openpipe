package no.trank.openpipe.solr.step;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision:712 $
 */
public class SolrDocumentProcessorTest extends TestCase {
   private static final Logger log = LoggerFactory.getLogger(SolrDocumentProcessorTest.class);

   public void testMatchesDynamicField() throws Exception {
/*
      SolrDocumentProcessor solrPoster = new SolrDocumentProcessor();
      HashSet<String> dynamicFields = new HashSet<String>();
      solrPoster.solrDynamicFields = dynamicFields;

      assertFalse(solrPoster.matchesDynamicField("abc_i"));
      assertFalse(solrPoster.matchesDynamicField("."));
      assertFalse(solrPoster.matchesDynamicField("*"));

      dynamicFields.add("*_i");
      assertFalse(solrPoster.matchesDynamicField("."));
      assertFalse(solrPoster.matchesDynamicField("*"));
      assertTrue(solrPoster.matchesDynamicField("abc_i"));
      assertFalse(solrPoster.matchesDynamicField("abci_i_a"));
      dynamicFields.add("*");
      assertTrue(solrPoster.matchesDynamicField("kjhfadslkh"));
      assertTrue(solrPoster.matchesDynamicField("lkjdf asdljk"));
*/
   }
}