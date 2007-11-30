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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotEmpty;

/**
 * Uses XPath to extract fields form a xml formatted field.
 * <p/>
 * Typical usage would be to set the fieldName(setFieldName()) to where the xml formatted text. Supply the
 * XPath -> field map in setXPathToFieldName(...).
 * <p/>
 * This would put the content of the fields that matches the XPath(s) into new fields with the supplied names.
 *
 * @version $Revision$
 */
public class ParseXMLXPath extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(ParseXML.class);
   private static final Pattern WS_PATTERN = Pattern.compile("\\s+");
   @NotEmpty
   private String fieldName;
   @NotEmpty
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
      super.prepare();

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

   /**
    * Gets the field name where the source xml is stored in the document.
    *
    * @return the field name where the source xml is stored in the document.
    */
   public String getFieldName() {
      return fieldName;
   }

   /**
    * Sets the field name where the source xml is stored in the document.
    *
    * @param fieldName the field name where the source xml is stored in the document.
    */
   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   /**
    * Gets the XPath to field name mappings.
    *
    * @return a map of XPath to field names
    */
   public Map<String, String> getXPathToFieldName() {
      return xPathToFieldName;
   }

   /**
    * Sets the XPath to field name mappings.
    *
    * @param xPathToFieldName a map of XPath to field names
    */
   public void setXPathToFieldName(Map<String, String> xPathToFieldName) {
      this.xPathToFieldName = xPathToFieldName;
   }

   /**
    * Gets the XPath instance used for looking up XPath matches.
    *
    * @return XPath instance used for looking up XPath matches
    */
   public XPath getXPath() {
      return xPath;
   }

   /**
    * Sets the XPath instance used for looking up XPath matches.
    * <p/>
    * I none are set this class will construct one using: XPathFactory.newInstance().newXPath();
    *
    * @param xPath the XPath instance used for looking up XPath matches.
    */
   public void setXPath(XPath xPath) {
      this.xPath = xPath;
   }

   /**
    * Gets if this step should fail if an xml parser error occurs.
    *
    * @return true if this step should fail if an xml parser error occurs.
    */
   public boolean isFailOnXMLError() {
      return failOnXMLError;
   }

   /**
    * Sets if this step should fail if an xml parser error occurs.
    * <p/>
    * Default is true
    *
    * @param failOnXMLError true if this step should fail if an xml parser error occurs.
    */
   public void setFailOnXMLError(boolean failOnXMLError) {
      this.failOnXMLError = failOnXMLError;
   }


   /**
    * Gets the xml DocumentBuilder instance to use for xml parsing.
    *
    * @return the xml DocumentBuilder instance to use for xml parsing.
    */
   public DocumentBuilder getBuilder() {
      return builder;
   }

   /**
    * Sets the xml DocumentBuilder instance to use for xml parsing.
    * <p/>
    * If this is not set, the step will construct one using: DocumentBuilderFactory.newInstance().newDocumentBuilder();
    *
    * @param builder the xml DocumentBuilder instance to use for xml parsing.
    */
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