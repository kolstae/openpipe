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
package no.trank.openpipe.parse.step;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.RawData;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;
import no.trank.openpipe.config.annotation.NullNotEmpty;
import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.PipelineParseData;

/**
 * @version $Revision$
 */
public class DocumentParser extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(DocumentParser.class);
   private final Set<String> loggedExt = new HashSet<String>();
   @NotEmpty
   private String fileNameField = "fileName";
   @NotNull
   private Map<String, Parser> parsers = Collections.emptyMap();
   @NotNull
   private List<Parser> fallbackParsers = Collections.emptyList();
   @NotNull
   private Set<String> ignoredFileExtensions = Collections.emptySet();
   @NotEmpty
   private String textField;
   @NullNotEmpty
   private String titleField;
   private boolean includeProperties;
   private boolean failOnParseFailure;
   private boolean stopOnParseFailure;

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final RawData data = doc.getRawData();
      if (data == null) {
         log.debug("No data to parse for doc {}", doc);
      } else if (data.isReleased()) {
         log.debug("Data released for doc {}", doc);
      } else {
         final String fileName = doc.getFieldValue(fileNameField);
         final String ext = findExtension(fileName);
         if (ignoredFileExtensions.contains(ext)) {
            log.debug("Ignored file extension '{}'", ext);
            return new PipelineStepStatus(PipelineStepStatusCode.FINISH);
         }
         final Parser parser = parsers.get(ext);
         final ParseData parseData = new PipelineParseData(data, includeProperties, fileName);
         if (parser == null || !parse(doc, parser, parseData)) {
            if (parser == null && loggedExt.add(ext)) {
               log.warn("No parser found for extension '{}'", ext);
            }
            if (parseWithFallbacks(doc, parseData)) {
               data.release();            
            } else if (failOnParseFailure) {
               throw new PipelineException("Parse failed for all parsers", getName());
            } else if (stopOnParseFailure) {
               return new PipelineStepStatus(PipelineStepStatusCode.FINISH);
            }
         } else {
            data.release();
         }
      }
      return PipelineStepStatus.DEFAULT;
   }

   private boolean parseWithFallbacks(Document doc, ParseData data) {
      for (Parser parser : fallbackParsers) {
         if (parse(doc, parser, data)) {
            return true;
         }
      }
      return false;
   }

   private boolean parse(Document doc, Parser parser, ParseData data) {
      try {
         final ParserResult result = parser.parse(data);
         doc.setFieldValue(textField, result.getText());
         if (titleField != null) {
            doc.setFieldValue(titleField, result.getTitle());
         }
         if (includeProperties) {
            final Map<String, String> properties = result.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
               doc.addFieldValue(entry.getKey(), entry.getValue());
            }
         }
         log.debug("{} parsed {}", parser.getClass().getName(), doc);
         return true;
      } catch (Exception e) {
         log.error("Problem parsing " + doc, e);
      }
      return false;
   }

   private static String findExtension(String fileName) {
      if (fileName != null) {
         final int idx = fileName.lastIndexOf('.');
         if (idx >= 0) {
            return fileName.substring(idx + 1).toLowerCase();
         } else {
            return "";
         }
      }
      return null;
   }

   @Override
   public void prepare() throws PipelineException {
      super.prepare();
      
      if (parsers.isEmpty() && fallbackParsers.isEmpty()) {
         throw new PipelineException("No parser configured", getName());
      }
   }

   @Override
   public void finish(boolean success) throws PipelineException {
      loggedExt.clear();
      closeParsers(parsers.values());
      closeParsers(fallbackParsers);
   }

   private static void closeParsers(Collection<Parser> parsers) {
      for (Parser parser : parsers) {
         if (parser instanceof Closeable) {
            try {
               ((Closeable)parser).close();
            } catch (IOException e) {
               // Ignoring
            }
         }
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public String getFileNameField() {
      return fileNameField;
   }

   public void setFileNameField(String fileNameField) {
      this.fileNameField = fileNameField;
   }

   public Map<String, Parser> getParsers() {
      return parsers;
   }

   public void setParsers(Map<String, Parser> parsers) {
      this.parsers = parsers;
   }

   public List<Parser> getFallbackParsers() {
      return fallbackParsers;
   }

   public void setFallbackParsers(List<Parser> fallbackParsers) {
      this.fallbackParsers = fallbackParsers;
   }

   public Set<String> getIgnoredFileExtensions() {
      return ignoredFileExtensions;
   }

   public void setIgnoredFileExtensions(Set<String> ignoredFileExtensions) {
      this.ignoredFileExtensions = ignoredFileExtensions;
   }

   public String getTextField() {
      return textField;
   }

   public void setTextField(String textField) {
      this.textField = textField;
   }

   public String getTitleField() {
      return titleField;
   }

   public void setTitleField(String titleField) {
      this.titleField = titleField;
   }

   public boolean isIncludeProperties() {
      return includeProperties;
   }

   public void setIncludeProperties(boolean includeProperties) {
      this.includeProperties = includeProperties;
   }

   public boolean isFailOnParseFailure() {
      return failOnParseFailure;
   }

   public void setFailOnParseFailure(boolean failOnParseFailure) {
      this.failOnParseFailure = failOnParseFailure;
   }

   public boolean isStopOnParseFailure() {
      return stopOnParseFailure;
   }

   public void setStopOnParseFailure(boolean stopOnParseFailure) {
      this.stopOnParseFailure = stopOnParseFailure;
   }
}
