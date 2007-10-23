package no.trank.openpipe.solr.step;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class SolrDocumentProcessorTest extends TestCase {

   public void testMatchesDynamicField() throws Exception {
      SolrDocumentProcessor solrPoster = new SolrDocumentProcessor();

      assertFalse(solrPoster.matchesDynamicField("abc_i"));
      assertFalse(solrPoster.matchesDynamicField("."));
      assertFalse(solrPoster.matchesDynamicField("*"));

      solrPoster.addDynamicField("*_i");
      assertFalse(solrPoster.matchesDynamicField("."));
      assertFalse(solrPoster.matchesDynamicField("*"));
      assertTrue(solrPoster.matchesDynamicField("abc_i"));
      assertFalse(solrPoster.matchesDynamicField("abci_i_a"));
      solrPoster.addDynamicField("*");
      assertTrue(solrPoster.matchesDynamicField("kjhfadslkh"));
      assertTrue(solrPoster.matchesDynamicField("lkjdf asdljk"));
   }
}