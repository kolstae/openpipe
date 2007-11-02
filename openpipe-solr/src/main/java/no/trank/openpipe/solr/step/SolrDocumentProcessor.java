package no.trank.openpipe.solr.step;

import java.io.IOException;
import java.io.InputStream;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;
import no.trank.openpipe.solr.SolrHttpDocumentPoster;
import no.trank.openpipe.solr.analysis.TokenSerializer;
import no.trank.openpipe.solr.xml.XmlInputStream;

/**
 * @version $Revision$
 */
public class SolrDocumentProcessor extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(SolrDocumentProcessor.class);

   private final Set<String> solrFields = new HashSet<String>();
   private final Set<Pattern> solrDynamicFields = new HashSet<Pattern>();

   private String solrSchemaUrl;
   private String idFieldName;

   private Map<String, String> inputToOuputFieldMap = Collections.emptyMap();
   private Set<String> excludeInputFields = Collections.emptySet();
   private Set<String> includeInputFields = Collections.emptySet();
   private Set<String> tokenizedFields = Collections.emptySet();
   private TokenSerializer serializer;
   private SolrHttpDocumentPoster documentPoster;
   private static final String BOOST_KEY = "boost";

   public SolrDocumentProcessor() {
      super("SolrPoster");
   }

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
         return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
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

   @Override
   public void prepare() throws PipelineException {
      if (solrSchemaUrl != null) {
         try {
            loadIndexSchema(new URL(solrSchemaUrl));
         } catch (Exception e) {
            throw new PipelineException(e);
         }
      }
   }

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
      InputStream inputStream = new XmlInputStream(url.openStream());
      try {

         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         org.w3c.dom.Document document = builder.parse(inputStream);

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
         addField(BOOST_KEY);

         if (idFieldName == null) {
            Node idNode = (Node) xpath.evaluate("/schema/uniqueKey", document, XPathConstants.NODE);
            idFieldName = idNode.getTextContent().trim();
         }

      } finally {
         try {
            inputStream.close();
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
