package no.trank.openpipe.step;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @version $Revision$
 */
public class ParseXMLXPath extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(ParseXML.class);
   private static final Pattern WS_PATTERN = Pattern.compile("\\s+");
   private String fieldName;
   private Map<String, String> xPathToFieldName = Collections.emptyMap();
   private List<XPathFieldName> xPaths;
   private boolean failOnXMLError = true;
   private DocumentBuilder builder;
   private XPath xPath;

   public ParseXMLXPath() {
      super("ParseXMLXPath");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final List<String> list = doc.getFieldValues(fieldName);
      for (String text : list) {
         try {
            final Node reader = builder.parse(new InputSource(new StringReader(text)));
            parseXML(doc, reader);
         } catch (IOException e) {
            handleException(e);
         } catch (SAXException e) {
            handleException(e);
         } catch (XPathExpressionException e) {
            handleException(e);
         }
      }
      return PipelineStepStatus.DEFAULT;
   }

   private void handleException(Exception e) throws PipelineException {
      if (failOnXMLError) {
         throw new PipelineException("Could not parse XML in field '" + fieldName + "'", e);
      } else {
         log.error("{}: Could not parse XML in field '" + fieldName + "'", e);
      }
   }

   private void parseXML(Document doc, Node node) throws XPathExpressionException {
      for (XPathFieldName e : xPaths) {
         final NodeList nl = (NodeList) e.getXPathExpression().evaluate(node, XPathConstants.NODESET);
         if (nl != null && nl.getLength() > 0) {
            buildNodeValue(nl, doc, e.getFieldname());
         }
      }
   }

   private static void buildNodeValue(NodeList nl, Document doc, String fieldName) {
      final StringBuilder buf = new StringBuilder(64);
      final int length = nl.getLength();
      for (int i = 0; i < length; i++) {
         buildNodeValue(nl.item(i), buf);
         if (buf.length() > 0) {
            final String value = buf.substring(0, buf.length() - 1);
            if (!isBlank(value)) {
               doc.addFieldValue(fieldName, value);
            }
            buf.setLength(0);
         }
      }
   }

   private static void buildNodeValue(Node n, StringBuilder buf) {
      if (!isBlank(n.getNodeValue())) {
         buf.append(n.getNodeValue());
         buf.append(' ');
      }
      if (n.hasChildNodes()) {
         final NodeList nl = n.getChildNodes();
         for (int i = 0; i < nl.getLength(); i++) {
            buildNodeValue(nl.item(i), buf);
         }
      }
   }

   private static boolean isBlank(String data) {
      return data == null || WS_PATTERN.matcher(data).matches();
   }

   @Override
   public void prepare() throws PipelineException {
      if (builder == null) {
         try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         } catch (ParserConfigurationException e) {
            throw new PipelineException(e);
         }
      }
      if (xPath == null) {
         xPath = XPathFactory.newInstance().newXPath();
      }
      if (xPathToFieldName.isEmpty()) {
         throw new PipelineException("No xpaths configured");
      }
      try {
         compileXPaths();
      } catch (XPathExpressionException e) {
         throw new PipelineException(e);
      }
   }

   private void compileXPaths() throws XPathExpressionException {
      xPaths = new ArrayList<XPathFieldName>(xPathToFieldName.size());
      for (Map.Entry<String, String> e : xPathToFieldName.entrySet()) {
         xPaths.add(new XPathFieldName(xPath.compile(e.getKey()), e.getValue()));
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public String getFieldName() {
      return fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   public Map<String, String> getXPathToFieldName() {
      return xPathToFieldName;
   }

   public void setXPathToFieldName(Map<String, String> xPathToFieldName) {
      this.xPathToFieldName = xPathToFieldName;
   }

   public XPath getXPath() {
      return xPath;
   }

   public void setXPath(XPath xPath) {
      this.xPath = xPath;
   }

   public boolean isFailOnXMLError() {
      return failOnXMLError;
   }

   public void setFailOnXMLError(boolean failOnXMLError) {
      this.failOnXMLError = failOnXMLError;
   }

   public DocumentBuilder getBuilder() {
      return builder;
   }

   public void setBuilder(DocumentBuilder builder) {
      this.builder = builder;
   }

   private static final class XPathFieldName {
      private final XPathExpression xPathExpression;
      private final String fieldname;

      private XPathFieldName(XPathExpression xPathExpression, String fieldname) {
         this.xPathExpression = xPathExpression;
         this.fieldname = fieldname;
      }

      public XPathExpression getXPathExpression() {
         return xPathExpression;
      }

      public String getFieldname() {
         return fieldname;
      }
   }
}