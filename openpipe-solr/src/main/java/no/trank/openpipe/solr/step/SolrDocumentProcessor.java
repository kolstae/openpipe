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
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.solr.SolrHttpDocumentPoster;
import no.trank.openpipe.solr.xml.XmlInputStream;

/**
 * @version $Revision$
 */
public class SolrDocumentProcessor extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(SolrDocumentProcessor.class);

   private Set<String> solrFields = new HashSet<String>();
   private Set<Pattern> solrDynamicFields = new HashSet<Pattern>();

   private String solrSchemaUrl;
   private String idFieldName;

   private Map<String, String> inputToOuputFieldMap = Collections.emptyMap();
   private Set<String> excludeInputFields = Collections.emptySet();
   private Set<String> includeInputFields = Collections.emptySet();

   private SolrHttpDocumentPoster documentPoster;

   public SolrDocumentProcessor() {
      super("SolrPoster");
   }

   public void setSolrSchemaUrl(String solrSchemaUrl) {
      this.solrSchemaUrl = solrSchemaUrl;
   }

   public PipelineStepStatus execute(Document doc) throws PipelineException {
      try {
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

         // Post the document
         documentPoster.postDocument(idFieldName, doc, solrOutputDoc);
         return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
      } catch (XMLStreamException e) {
         throw new PipelineException("Could not generate xml", e);
      }
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
      try {
         documentPoster.finish();
      } catch (XMLStreamException e) {
         throw new PipelineException("Could not write xml", e);
      }
   }

   public void setExcludeInputFields(Set<String> excludeInputFields) {
      this.excludeInputFields = excludeInputFields;
   }

   public void setIncludeInputFields(Set<String> includeInputFields) {
      this.includeInputFields = includeInputFields;
   }

   public void setIdFieldName(String idFieldName) {
      this.idFieldName = idFieldName;
   }

   public void setDocumentPoster(SolrHttpDocumentPoster documentPoster) {
      this.documentPoster = documentPoster;
   }

   public String getRevision() {
      return "$Revision$";
   }

   public void setInputToOuputFieldMap(Map<String, String> inputToOuputFieldMap) {
      this.inputToOuputFieldMap = inputToOuputFieldMap;
   }

   private void addField(Document doc, String inputField, HashMap<String, List<String>> solrOutputDoc) throws PipelineException {
      String ouputField = getOuputFieldName(inputField);
      if (solrSchemaUrl == null || solrFields.contains(inputField) || matchesDynamicField(inputField)) {
         List<String> fieldValueList = solrOutputDoc.get(inputField);
         if (fieldValueList == null) {
            fieldValueList = new ArrayList<String>();
            solrOutputDoc.put(inputField, fieldValueList);
         }
         fieldValueList.addAll(doc.getFieldValues(ouputField));
      } else {
         log.debug("Field '{}' does not exist in solr schema, and does not match a dynamic field. Skipped.", ouputField);
      }
   }

   private String getOuputFieldName(String inputField) {
      final String mappedName = inputToOuputFieldMap.get(inputField);
      return mappedName == null ? inputField : mappedName;
   }

   boolean matchesDynamicField(String inputField) {
      for (Pattern dynamicField : solrDynamicFields) {
         if (dynamicField.matcher(inputField).matches()) {
            return true;
         }
      }
      return false;
   }

   private void loadIndexSchema(URL url) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
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
               solrFields.add(name);
            } else if ("dynamicField".equals(nodeName)) {
               solrDynamicFields.add(Pattern.compile(name.replaceAll("\\*", "\\.*")));
            }
         }
         solrFields.add("boost"); // Adding "always present" field

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
}
