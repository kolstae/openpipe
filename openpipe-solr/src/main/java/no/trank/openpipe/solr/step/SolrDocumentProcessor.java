/*
 * Copyright 2007  T-Rank AS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.trank.openpipe.solr.step;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;
import no.trank.openpipe.config.annotation.NotNull;
import no.trank.openpipe.config.annotation.NullNotEmpty;
import no.trank.openpipe.solr.SolrHttpDocumentPoster;
import no.trank.openpipe.solr.analysis.TokenSerializer;
import no.trank.openpipe.solr.xml.XmlInputStream;

/**
 * A <tt>PipelineStep</tt> that posts a document to Solr.
 * 
 * <p>{@linkplain Document}s are converted to <a href="http://wiki.apache.org/solr/UpdateXmlMessages">solr-update-xml
 * </a>. An URL to solr's schema.xml can be configured to validate field-names and dynamic fields. Typical URL being: 
 * <tt>http://somehost:8983/solr/admin/get-file.jsp?file=schema.xml</tt></p>
 * 
 * <p>There are two ways control what fields are included in the XML: 
 * {@link #setExcludeInputFields(Set) excludeInputFields} and {@link #setIncludeInputFields(Set) includeInputFields}. 
 * When <tt>includeInputFields</tt> is a not-empty set, only field-names in this set is included in the XML. When 
 * <tt>includeInputFields</tt> is an empty set and <tt>excludeInputFields</tt> is not empty, field-names in 
 * <tt>excludeInputFields</tt> is excluded from the XML</p>
 * 
 * <p>It's possible to map a document field-name to a solr field-name using 
 * {@link #setInputToOuputFieldMap(Map) inputToOuputFieldMap}. Mapping is applied after <tt>include</tt>-/
 * <tt>excludeInputFields</tt></p>
 * 
 * <p>To set document boost (&lt;doc boost=&quot;2.0&quot;/&gt;), add a field that, after mapping, has the name 
 * <tt>&quot;boost&quot;</tt>.</p>
 * 
 * <p><b>Note</b> Field boost is currently <em>not</em> supported</p>
 * 
 * <p><em>Example:</em>
 * <pre>
 * Document doc = new Document();
 * doc.setOperation(DocumentOperation.ADD_VALUE);
 * doc.setFieldValue("boost", "2.0");
 * doc.setFieldValue("url", "http://this/is/a/url");
 * doc.setFieldValue("title", "Title");
 * doc.setFieldValue("text", "This is the text");
 * doc.setFieldValue("ignored", "This text is ignored");
 * ...
 * SolrDocumentProcessor sdp = new SolrDocumentProcessor();
 * sdp.setExcludeInputFields(Collections.singelton("ignored"));
 * sdp.setInputToOuputFieldMap(Collections.singletonMap("url", "id"));
 * sdp.execute(doc);
 * </pre>
 * gives the XML:
 * <pre>
 * &lt;add&gt;
 *   &lt;doc boost="2.0"&gt;
 *     &lt;field name="id"&gt;http://this/is/a/url&lt;/field&gt;
 *     &lt;field name="title"&gt;Title&lt;/field&gt;
 *     &lt;field name="text"&gt;This is the text&lt;/field&gt;
 *   &lt;/doc&gt;
 * &lt;/add&gt;
 * </pre>
 * </p>
 * 
 * @version $Revision$
 */
public class SolrDocumentProcessor extends BasePipelineStep {
   protected static final String BOOST_KEY = "boost";
   private static final Logger log = LoggerFactory.getLogger(SolrDocumentProcessor.class);

   private final Set<String> solrFields = new HashSet<String>();
   private final Set<Pattern> solrDynamicFields = new HashSet<Pattern>();

   @NullNotEmpty
   private String solrSchemaUrl;
   @NullNotEmpty
   private String idFieldName;

   @NotNull
   private Map<String, String> inputToOuputFieldMap = Collections.emptyMap();
   @NotNull
   private Set<String> excludeInputFields = Collections.emptySet();
   @NotNull
   private Set<String> includeInputFields = Collections.emptySet();
   @NotNull
   private Set<String> tokenizedFields = Collections.emptySet();
   private TokenSerializer serializer;
   @NotNull
   private SolrHttpDocumentPoster documentPoster;
   private HttpClient httpClient;

   /**
    * Creates a <tt>SolrDocumentProcessor</tt> with the name <tt>&quot;SolrPoster&quot;</tt>.
    */
   public SolrDocumentProcessor() {
      super("SolrPoster");
   }

   /**
    * Converts a document to XML and posts it to solr.
    * 
    * @param doc the document to process.
    * 
    * @return <tt>PipelineStepStatus.DEFAULT</tt>.
    * 
    * @throws PipelineException if an error occures during processing or posting.
    * 
    * @see SolrDocumentProcessor
    */
   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      try {
         // Post the document
         if (DocumentOperation.DELETE_VALUE.equals(doc.getOperation())) {
            if (idFieldName != null) {
               documentPoster.delete(doc.getFieldValues(idFieldName));
            } else {
               log.warn("idFieldName not set -> delete not supported - ignoring");
            }
         } else {
            final HashMap<String, List<String>> solrOutputDoc = new HashMap<String, List<String>>();
            // Get what field we want to post to solr
            for (String inputField : doc.getFieldNames()) {
               if (!includeInputFields.isEmpty()) {
                  if (includeInputFields.contains(inputField)) {
                     addField(doc, inputField, solrOutputDoc);
                  }
               } else if (!excludeInputFields.isEmpty()) {
                  if (!excludeInputFields.contains(inputField)) {
                     addField(doc, inputField, solrOutputDoc);
                  }
               } else {
                  addField(doc, inputField, solrOutputDoc);
               }
            }
            documentPoster.add(solrOutputDoc, findDocAttributes(solrOutputDoc));
         }
         return PipelineStepStatus.DEFAULT;
      } catch (XMLStreamException e) {
         throw new PipelineException("Could not generate xml", e);
      }
   }

   private static Map<String, String> findDocAttributes(HashMap<String, List<String>> solrOutputDoc) {
      final List<String> boostList = solrOutputDoc.remove(BOOST_KEY);
      final Map<String, String> attribs;
      if (boostList != null && !boostList.isEmpty()) {
         if (boostList.size() > 1) {
            log.warn("Got multiple boost values {} for document", boostList);
         }
         attribs = Collections.singletonMap(BOOST_KEY, boostList.get(0));
      } else {
         attribs = Collections.emptyMap();
      }
      return attribs;
   }

   /**
    * Loads <tt>schema.xml</tt> if {@link #getSolrSchemaUrl() solrSchemaUrl} is not <tt>null</tt>. 
    * 
    * @throws PipelineException if {@link #getDocumentPoster() documentPoster} is <tt>null</tt>, if schema.xml could not 
    * be parsed or if {@link #getTokenizedFields() tokenizedFields} is <em>not</em> empty and 
    * {@link #getSerializer() serializer} is <tt>null</tt>. 
    */
   @Override
   public void prepare() throws PipelineException {
      super.prepare();
      try {
         documentPoster.prepare();
      } catch (MalformedURLException e) {
         throw new PipelineException("Post url is malformed", e);
      } catch (IOException e) {
         throw new PipelineException(e);
      }

      if (solrSchemaUrl != null) {
         try {
            loadIndexSchema(new URL(solrSchemaUrl));
         } catch (Exception e) {
            throw new PipelineException(e);
         }
      }
      addField(BOOST_KEY); // Needed even if there is no schemaUrl
      if (!tokenizedFields.isEmpty() && serializer == null) {
         throw new PipelineException("TokenizedFields set, but no serializer configured");
      }

      if (httpClient == null) {
         httpClient = new HttpClient();
      }
   }

   /**
    * Finishes this batch, by posting outstanding documents (if any) to solr. Cleans up any resources.
    * 
    * @throws PipelineException if post to solr failed.
    */
   @Override
   public void finish(boolean success) throws PipelineException {
      if (serializer != null) {
         try {
            serializer.close();
         } catch (IOException e) {
            // Ignoring
         }
      }
      try {
         documentPoster.finish();
      } catch (XMLStreamException e) {
         throw new PipelineException("Could not write xml", e);
      }
   }

   public String getSolrSchemaUrl() {
      return solrSchemaUrl;
   }

   public void setSolrSchemaUrl(String solrSchemaUrl) {
      this.solrSchemaUrl = solrSchemaUrl;
   }

   public Set<String> getExcludeInputFields() {
      return excludeInputFields;
   }

   public void setExcludeInputFields(Set<String> excludeInputFields) {
      this.excludeInputFields = excludeInputFields;
   }

   public Set<String> getIncludeInputFields() {
      return includeInputFields;
   }

   public void setIncludeInputFields(Set<String> includeInputFields) {
      this.includeInputFields = includeInputFields;
   }

   public String getIdFieldName() {
      return idFieldName;
   }

   public void setIdFieldName(String idFieldName) {
      this.idFieldName = idFieldName;
   }

   public SolrHttpDocumentPoster getDocumentPoster() {
      return documentPoster;
   }

   public void setDocumentPoster(SolrHttpDocumentPoster documentPoster) {
      this.documentPoster = documentPoster;
   }

   public Map<String, String> getInputToOuputFieldMap() {
      return inputToOuputFieldMap;
   }

   public void setInputToOuputFieldMap(Map<String, String> inputToOuputFieldMap) {
      this.inputToOuputFieldMap = inputToOuputFieldMap;
   }

   public Set<String> getTokenizedFields() {
      return tokenizedFields;
   }

   public void setTokenizedFields(Set<String> tokenizedFields) {
      this.tokenizedFields = tokenizedFields;
   }

   public TokenSerializer getSerializer() {
      return serializer;
   }

   public void setSerializer(TokenSerializer serializer) {
      this.serializer = serializer;
   }

   /**
    * Gets an <em>unmodifiable</em> set of field-names.
    * 
    * @return an <em>unmodifiable</em> set of field-names.
    */
   protected Set<String> getSolrFields() {
      return Collections.unmodifiableSet(solrFields);
   }

   public void setHttpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   protected void addField(Document doc, String inputField, HashMap<String, List<String>> solrOutputDoc) 
         throws PipelineException {
      final String ouputField = getOuputFieldName(inputField);
      if (solrSchemaUrl == null || solrFields.contains(ouputField) || matchesDynamicField(ouputField)) {
         List<String> fieldValueList = solrOutputDoc.get(ouputField);
         if (fieldValueList == null) {
            fieldValueList = new ArrayList<String>();
            solrOutputDoc.put(ouputField, fieldValueList);
         }
         if (tokenizedFields.contains(inputField)) {
            fieldValueList.addAll(serialize(doc.getFields(inputField)));
         } else {
            fieldValueList.addAll(doc.getFieldValues(inputField));
         }
      } else if (log.isDebugEnabled()) {
         log.debug("Field '{}' does not exist in solr schema, and does not match a dynamic field. Skipped.", ouputField);
      }
   }

   private List<String> serialize(List<AnnotatedField> fields) {
      final List<String> list = new ArrayList<String>(fields.size());
      for (AnnotatedField field : fields) {
         list.add(serializer.serialize(field));
      }
      return list;
   }

   protected String getOuputFieldName(String inputField) {
      final String mappedName = inputToOuputFieldMap.get(inputField);
      return mappedName == null ? inputField : mappedName;
   }

   protected boolean matchesDynamicField(String inputField) {
      for (Pattern dynamicField : solrDynamicFields) {
         if (dynamicField.matcher(inputField).matches()) {
            return true;
         }
      }
      return false;
   }

   private void loadIndexSchema(URL url) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
      solrFields.clear();
      solrDynamicFields.clear();

      InputStream sIn;
      if (url.getProtocol().equals("file")) {
         sIn = url.openStream();
      } else {
         GetMethod get = new GetMethod(url.toExternalForm());
         httpClient.executeMethod(get);
         sIn = get.getResponseBodyAsStream();
      }
      final InputStream in = new XmlInputStream(sIn);
      try {

         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         org.w3c.dom.Document document = builder.parse(in);

         final XPath xpath = XPathFactory.newInstance().newXPath();
         final NodeList nodes = (NodeList) xpath.evaluate("/schema/fields/field | /schema/fields/dynamicField", document,
               XPathConstants.NODESET);

         for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            final String name = ((Element) node).getAttribute("name");
            final String nodeName = node.getNodeName();
            if ("field".equals(nodeName)) {
               addField(name);
            } else if ("dynamicField".equals(nodeName)) {
               addDynamicField(name);
            }
         }

         if (idFieldName == null) {
            Node idNode = (Node) xpath.evaluate("/schema/uniqueKey", document, XPathConstants.NODE);
            idFieldName = idNode.getTextContent().trim();
         }

      } finally {
         try {
            in.close();
         } catch (Exception e) {
            // Do nothing
         }
      }
   }

   protected boolean addField(String fieldName) {
      return solrFields.add(fieldName);
   }

   protected boolean addDynamicField(String fieldPattern) {
      return solrDynamicFields.add(Pattern.compile(fieldPattern.replaceAll("\\*", "\\.*")));
   }
}
