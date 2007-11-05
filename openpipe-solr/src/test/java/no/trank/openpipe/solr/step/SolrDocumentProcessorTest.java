package no.trank.openpipe.solr.step;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;
import no.trank.openpipe.solr.SolrHttpDocumentPoster;
import static no.trank.openpipe.solr.step.SolrDocumentProcessor.BOOST_KEY;

/**
 * @version $Revision$
 */
public class SolrDocumentProcessorTest extends TestCase {
   private static final String TEST_SCHEMA = "/solr/schema.xml";
   private TestPoster testPoster;
   private SolrDocumentProcessor sdp;

   public void testMatchesDynamicField() throws Exception {

      assertFalse(sdp.matchesDynamicField("abc_i"));
      assertFalse(sdp.matchesDynamicField("."));
      assertFalse(sdp.matchesDynamicField("*"));

      sdp.addDynamicField("*_i");
      assertFalse(sdp.matchesDynamicField("."));
      assertFalse(sdp.matchesDynamicField("*"));
      assertTrue(sdp.matchesDynamicField("abc_i"));
      assertFalse(sdp.matchesDynamicField("abci_i_a"));
      sdp.addDynamicField("*");
      assertTrue(sdp.matchesDynamicField("kjhfadslkh"));
      assertTrue(sdp.matchesDynamicField("lkjdf asdljk"));
   }

   public void testExecute() throws Exception {
      setupSchema(TEST_SCHEMA);
      sdp.setInputToOuputFieldMap(Collections.singletonMap("mappedURL", "url"));
      sdp.prepare();
      final Document doc = new Document();
      doc.setOperation(DocumentOperation.ADD_VALUE);
      doc.setFieldValue(BOOST_KEY, "2.0");
      doc.setFieldValue("id", "1");
      doc.setFieldValue("ignored", "ignorredValue"); // Not included in solr-doc
      doc.setFieldValue("title", "Test Title");
      doc.setFieldValue("content", "Test Content");
      doc.setFieldValues("url", Arrays.asList("url1", "url2"));
      doc.setFieldValues("mappedURL", Arrays.asList("url3", "url4"));
      final PipelineStepStatus status = sdp.execute(doc);
      assertNotNull(status);
      assertEquals(PipelineStepStatusCode.CONTINUE, status.getStatusCode());
      final Map<String, String> attribs = testPoster.getAttribs();
      assertEquals(1, attribs.size());
      assertEquals("2.0", attribs.get(BOOST_KEY));
      final HashMap<String, List<String>> solrDoc = testPoster.getSolrOutputDoc();
      assertFalse(solrDoc.containsKey("ignored"));
      assertFalse(solrDoc.containsKey(BOOST_KEY));
      assertEquals(4, solrDoc.get("url").size());
   }

   public void testPrepare() throws Exception {
      setupSchema("/solr/no-schema.xml");
      try {
         sdp.prepare();
         fail("Should fail on invalid schema");
      } catch (PipelineException e) {
         // Should throw execption
      }
      setupSchema(TEST_SCHEMA);
      sdp.prepare();
      assertEquals("idFieldName", "id", sdp.getIdFieldName());
      final Set<String> fields = sdp.getSolrFields();
      assertTrue("solrFields", fields.containsAll(Arrays.asList("id", "url", "title", "content", "lastModified")));
      assertTrue(BOOST_KEY, fields.contains(BOOST_KEY));
      // Test validate config
      sdp.setTokenizedFields(Collections.singleton("test"));
      try {
         sdp.prepare();
         fail("Should fail without serializer");
      } catch (PipelineException e) {
         // Should throw execption
      }
      sdp.setTokenizedFields(Collections.<String>emptySet());
      sdp.setDocumentPoster(null);
      try {
         sdp.prepare();
         fail("Should fail without documentPoster");
      } catch (PipelineException e) {
         // Should throw execption
      }
   }

   private void setupSchema(String name) {
      final URL schemaURL = getClass().getResource(name);
      assertNotNull("Missing resource: '" + name + '\'', schemaURL);
      sdp.setSolrSchemaUrl(schemaURL.toExternalForm());
   }

   @Override
   protected void setUp() throws Exception {
      testPoster = new TestPoster();
      sdp = new SolrDocumentProcessor();
      sdp.setDocumentPoster(testPoster);
   }

   private static class TestPoster extends SolrHttpDocumentPoster {
      private HashMap<String, List<String>> solrOutputDoc;
      private Map<String, String> attribs;

      @Override
      public void add(HashMap<String, List<String>> solrOutputDoc, Map<String, String> attribs) 
            throws XMLStreamException, PipelineException {
         this.solrOutputDoc = solrOutputDoc;
         this.attribs = attribs;
      }

      public HashMap<String, List<String>> getSolrOutputDoc() {
         return solrOutputDoc;
      }

      public Map<String, String> getAttribs() {
         return attribs;
      }
   }
}