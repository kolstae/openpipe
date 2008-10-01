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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Formatter;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;
import no.trank.openpipe.step.xml.DocumentWriter;
import no.trank.openpipe.step.xml.XMLStreamDocWriter;

/**
 * Uses a <tt>DocumentWriter</tt> to write documents to a set of XML-files.
 *
 * @version $Revision$
 */
public class WriteXML extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(WriteXML.class);
   private boolean failOnXMLError;
   private int maxDocsPerFile = -1;
   @NotEmpty
   private String rootElementName;
   @NotEmpty
   private String docElementName;
   @NotNull
   private File directory;
   @NotNull
   private Map<String, String> fieldToAttributes = Collections.emptyMap();
   @NotNull
   private Map<String, String> fieldToElements = Collections.emptyMap();
   private DocumentWriter writer;
   private int docCount;
   private int fileCount;

   public WriteXML() {
      super("WriteXML");
   }

   @Override
   public void prepare() throws PipelineException {
      super.prepare();

      if (fieldToAttributes.isEmpty() && fieldToElements.isEmpty()) {
         throw new PipelineException("No fields are configured!", getName());
      }

      if (directory.isFile()) {
         throw new PipelineException(directory + " is a file", getName());
      } else if (!directory.exists() && !directory.mkdir()) {
         throw new PipelineException("Could not create directory" + directory, getName());
      } else if (!directory.canWrite()) {
         throw new PipelineException("Directory " + directory + " is not writable", getName());
      }

      if (writer == null) {
         writer = new XMLStreamDocWriter();
      }

      setupWriter();
   }

   @Override
   public void finish(boolean success) throws PipelineException {
      closeWriter();
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      ensureWriter();
      writeDocument(doc);

      return PipelineStepStatus.DEFAULT;
   }

   private void writeDocument(Document doc) throws PipelineException {
      try {
         writer.startElement(docElementName);
         addAttributes(doc);
         addElements(doc);
         writer.endElement();
      } catch (XMLStreamException e) {
         if (failOnXMLError) {
            throw new PipelineException("Could not write document", e, getName());
         }
         log.error("Could not write document", e);
      }
   }

   private void addElements(Document doc) throws XMLStreamException {
      for (Map.Entry<String, String> e : fieldToElements.entrySet()) {
         writer.addElement(e.getValue(), doc.getFieldValues(e.getKey()));
      }
   }

   private void addAttributes(Document doc) throws XMLStreamException {
      for (Map.Entry<String, String> e : fieldToAttributes.entrySet()) {
         writer.addAttribute(e.getValue(), doc.getFieldValues(e.getKey()));
      }
   }

   private void ensureWriter() throws PipelineException {
      if (maxDocsPerFile > 0 && docCount++ > maxDocsPerFile) {
         docCount = 0;
         closeWriter();
         setupWriter();
      }
   }

   private void closeWriter() throws PipelineException {
      if (writer != null) {
         try {
            writer.endDocument();
            try {
               writer.close();
            } catch (IOException e) {
               log.error("Unable to close writer", e);
            }
         } catch (XMLStreamException e) {
            throw new PipelineException("Could not close writer", e, getName());
         }
      }
   }

   private void setupWriter() throws PipelineException {
      try {
         writer.reset(new File(directory, createFileName()));
         writer.startDocument(rootElementName);
      } catch (XMLStreamException e) {
         throw new PipelineException("Could not setup writer", e, getName());
      } catch (FileNotFoundException e) {
         throw new PipelineException(e, getName());
      }
   }

   private String createFileName() {
      return new Formatter().format("%1$04d.xml", fileCount++).toString();
   }

   /**
    * Sets whether an error in writing will cause a <tt>PipelineException</tt> to be thrown or not.
    *
    * @param failOnXMLError <tt>true</tt> if a <tt>PipelineException</tt> should be thrown.
    */
   public void setFailOnXMLError(boolean failOnXMLError) {
      this.failOnXMLError = failOnXMLError;
   }

   /**
    * Sets the name of the root element of the XML-file(s).
    *
    * @param rootElementName name of the root element of the XML-file(s).
    */
   public void setRootElementName(String rootElementName) {
      this.rootElementName = rootElementName;
   }

   /**
    * Sets the name of the document element of the XML-file(s).
    *
    * @param rootElementName name of the document element of the XML-file(s).
    */
   public void setDocElementName(String docElementName) {
      this.docElementName = docElementName;
   }

   /**
    * Sets the directory where the XML-file(s) will be written.
    * <br/>
    * <tt>directory</tt> or <tt>directory.getParent()</tt> must be writable.
    *
    * @param directory the directory where the XML-file(s) will be written.
    */
   public void setDirectory(File directory) {
      this.directory = directory;
   }

   /**
    * Sets the maximum number of documents in an XML-file.
    * <br/>
    * Default value is <tt>-1</tt>.
    *
    * @param maxDocsPerFile the maximum number of documents in an XML-file. <tt>&lt;= 0</tt> specifies no limit.
    */
   public void setMaxDocsPerFile(int maxDocsPerFile) {
      this.maxDocsPerFile = maxDocsPerFile;
   }

   /**
    * Sets which fields will be rendered as attributes.
    * <br/>
    * The <tt>keys</tt> of the map gives the field-names. The <tt>value</tt> of the <tt>key</tt> gives the
    * attribute-name.
    *
    * @param fieldToAttributes the field-name to attribute-name map.
    */
   public void setFieldToAttributes(Map<String, String> fieldToAttributes) {
      this.fieldToAttributes = fieldToAttributes;
   }

   /**
    * Sets which fields will be rendered as elements.
    * <br/>
    * The <tt>keys</tt> of the map gives the field-names. The <tt>value</tt> of the <tt>key</tt> gives the element-name.
    *
    * @param fieldToElements the field-name to element-name map.
    */
   public void setFieldToElements(Map<String, String> fieldToElements) {
      this.fieldToElements = fieldToElements;
   }

   /**
    * Sets the <tt>DocumentWriter</tt> that writes the acctual XML-file(s).
    * <br/>
    * If no writer is specified a {@link no.trank.openpipe.step.xml.XMLStreamDocWriter} is used.
    *
    * @param writer the field-name to element-name map.
    */
   public void setWriter(DocumentWriter writer) {
      this.writer = writer;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }
}