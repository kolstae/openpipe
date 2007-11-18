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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import no.trank.openpipe.api.MultiInputOutputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This step converts date formats using {@link SimpleDateFormat}.
 *
 * @version $Revision$
 */
public class ConvertDate extends MultiInputOutputFieldPipelineStep {
   private static Logger log = LoggerFactory.getLogger(ConvertDate.class);
   @NotEmpty
   private LinkedHashMap<String, String> patternMap;
   private List<FormatPair> formats;
   private boolean failOnError;
   private boolean blankError;

   public ConvertDate() {
      super("ConvertDate", false);
   }

   @Override
   public void prepare() throws PipelineException {
      super.prepare();

      formats = new ArrayList<FormatPair>(patternMap.size());
      try {
         for (Entry<String, String> e : patternMap.entrySet()) {
            formats.add(new FormatPair(e.getKey(), e.getValue()));
         }
      } catch (RuntimeException e) {
         throw new PipelineException(e);
      }
   }

   @Override
   protected void process(Document doc, String inputFieldName, List<AnnotatedField> inputFields, String outputFieldName)
         throws PipelineException {
      if (!inputFields.isEmpty()) {
         List<String> output = new ArrayList<String>();
         for (AnnotatedField field : inputFields) {
            try {
               output.add(getOutputValue(inputFieldName, field.getValue()));
            } catch (ParseException e) {
               if (failOnError) {
                  throw new PipelineException("Could not parse date " + field.getValue());
               }
            }
         }
         
         doc.setFieldValues(outputFieldName, output);
      } else if (blankError) {
         throw new PipelineException("Field '" + inputFieldName + "' is empty");
      }
   }      

   @Override
   public void finish(boolean success) throws PipelineException {
      formats = null;
   }
   
   private String getOutputValue(String fromFieldName, String fromValue) throws ParseException {
      for (FormatPair format : formats) {
         final SimpleDateFormat from = format.getFrom();
         final SimpleDateFormat to = format.getTo();
            
         String ret = to.format(from.parse(fromValue));
         if (log.isDebugEnabled()) {
            log.debug("Parsed field '" + fromFieldName + "' with pattern '" + from.toPattern() +
                      ". Output pattern: '" + to.toPattern() + "'");
         }
         return ret;
      }
      
      return null; // will never be reached
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   /**
    * Gets the ordered map of from/to date format pairs.
    * 
    * @return the pattern map
    */
   public LinkedHashMap<String, String> getPatternMap() {
      return patternMap;
   }

   /**
    * Sets the ordered map of from/to date format pairs. When applied to the input, consecutive pairs act as a fallback
    * should the previous one generate an error. The step only errors if the last pair errors.
    *
    * @param patternMap an ordered map containing the from/to format pairs
    */
   public void setPatternMap(LinkedHashMap<String, String> patternMap) {
      this.patternMap = patternMap;
   }

   /**
    * Gets whether an exception will be thrown if an error occurs.
    *
    * @return <code>true</code> if an exception will be thrown, <code>false</code> otherwise
    */
   public boolean isFailOnError() {
      return failOnError;
   }

   /**
    * Sets whether an exception will be thrown if an error occurs.
    *
    * @param failOnError
    */
   public void setFailOnError(boolean failOnError) {
      this.failOnError = failOnError;
   }

   /**
    * Gets whether a blank input field will be treated as an error.
    *
    * @return <code>true</code> if a blank input field will be treated as an error, <code>false</code> otherwise
    */
   public boolean isBlankError() {
      return blankError;
   }

   /**
    * Sets whether a blank input field will be treated as an error.
    *
    * @param blankError
    */
   public void setBlankError(boolean blankError) {
      this.blankError = blankError;
   }

   private static final class FormatPair {
      private final SimpleDateFormat from;
      private final SimpleDateFormat to;

      public FormatPair(String fromPattern, String toPattern) {
         from = new SimpleDateFormat(fromPattern);
         to = new SimpleDateFormat(toPattern);
      }

      public SimpleDateFormat getFrom() {
         return from;
      }

      public SimpleDateFormat getTo() {
         return to;
      }
   }
}