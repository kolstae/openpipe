package no.trank.openpipe.solr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import no.trank.openpipe.api.PipelineException;

/**
 * @version $Revision$
 */
public class SolrHttpDocumentPoster {
   private int docsPerCommit = 10000;
   private int docsPerPost = 0;
   private int unpostedDocs = 0;
   private int uncommitedDocs = 0;
   private String postUrl;
   private String encoding = "UTF-8";
   private HttpClient httpClient = new HttpClient();
   private SolrXmlDocumentWriter solrDocumentWriter;
   private boolean inAdd = false;
   private XMLBufferRequestEntity buf;
   private UpdateOptions updateOptions = new UpdateOptions();

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public void setPostUrl(String postUrl) {
      this.postUrl = postUrl;
   }

   public void setDocsPerCommit(int docsPerCommit) {
      this.docsPerCommit = docsPerCommit;
   }

   public void setDocsPerPost(int docsPerPost) {
      this.docsPerPost = docsPerPost;
   }

   public UpdateOptions getUpdateOptions() {
      return updateOptions;
   }

   public void setUpdateOptions(UpdateOptions updateOptions) {
      this.updateOptions = updateOptions;
   }

   public void add(HashMap<String, List<String>> solrOutputDoc, Map<String, String> attribs) throws XMLStreamException, PipelineException {
      addDocument(solrOutputDoc, attribs);
      if (++unpostedDocs >= docsPerPost) {
         endAdd();
      }
      if (++uncommitedDocs >= docsPerCommit) {
         commit();
      }
   }

   public void optimize() throws PipelineException, XMLStreamException {
      endAdd();
      openPostStream();
      solrDocumentWriter.optimize();
      closePostStream();
   }

   public void commit() throws XMLStreamException, PipelineException {
      endAdd();
      openPostStream();
      solrDocumentWriter.commit();
      closePostStream();
      uncommitedDocs = 0;
   }

   public void finish() throws XMLStreamException, PipelineException {
      if (uncommitedDocs > 0) {
         commit();
      }
   }

   public void delete(List<String> fieldValueList) throws PipelineException, XMLStreamException {
      endAdd();
      if (fieldValueList != null && !fieldValueList.isEmpty()) {
         openPostStream();
         solrDocumentWriter.deleteById(fieldValueList);
         closePostStream();
      } else {
         throw new PipelineException("Can not delete document. The id field is not set.");
      }
      if (++uncommitedDocs >= docsPerCommit) {
         commit();
      }
   }

   private void addDocument(HashMap<String, List<String>> solrOutputDoc, Map<String, String> attribs) 
         throws XMLStreamException, PipelineException {
      startAdd();
      solrDocumentWriter.startDoc(attribs);
      for (Map.Entry<String, List<String>> fieldEntry : solrOutputDoc.entrySet()) {
         for (String fieldValue : fieldEntry.getValue()) {
            solrDocumentWriter.writeField(fieldEntry.getKey(), fieldValue);
         }
      }
      solrDocumentWriter.endDoc();
   }

   private void startAdd() throws PipelineException, XMLStreamException {
      if (!inAdd) {
         openPostStream();
         solrDocumentWriter.startAdd();
         inAdd = true;
      }
   }

   private void endAdd() throws XMLStreamException, PipelineException {
      if (inAdd) {
         solrDocumentWriter.endAdd();
         inAdd = false;
         closePostStream();
         unpostedDocs = 0;
      }
   }

   private void openPostStream() throws PipelineException {
      try {
         if (buf == null) {
            buf = new XMLBufferRequestEntity(4096);
         } else {
            buf.reset();
         }
         solrDocumentWriter = new SolrXmlDocumentWriter(new OutputStreamWriter(buf, encoding), updateOptions);
      } catch (IOException e) {
         throw new PipelineException("Could not create post streams", e);
      } catch (XMLStreamException e) {
         throw new PipelineException("Could not create solrXmlDocWriter", e);
      }
   }

   private void closePostStream() throws PipelineException {
      // Closing the writer
      if (solrDocumentWriter != null) {
         try {
            solrDocumentWriter.close();
         } catch (Exception e) {
            // Do nothing
         }
         solrDocumentWriter = null;
      }

      // Flushing the data to solr
      try {
         final PostMethod postMethod = new PostMethod(postUrl);
         try {
            postMethod.setRequestEntity(buf);
            int status = httpClient.executeMethod(postMethod);
            if (status < 200 || status >= 300) {
               throw new PipelineException("Solr post returned status: " + status + "\n" + postMethod.getResponseBodyAsString());
            }
         } finally {
            postMethod.releaseConnection();
         }
      } catch (IOException e) {
         throw new PipelineException("Could not post document(s) to solr", e);
      } finally {
         try {
            buf.close();
         } catch (Exception e) {
            // Do nothing
         }
         buf = null;
      }
   }

   private static class XMLBufferRequestEntity extends ByteArrayOutputStream implements RequestEntity {
      
      private XMLBufferRequestEntity(int size) {
         super(size);
      }

      @Override
      public boolean isRepeatable() {
         return true;
      }

      @Override
      public void writeRequest(OutputStream out) throws IOException {
         out.write(buf, 0, count);
      }

      @Override
      public long getContentLength() {
         return count;
      }

      @Override
      public String getContentType() {
         return "text/xml";
      }
   }
}
