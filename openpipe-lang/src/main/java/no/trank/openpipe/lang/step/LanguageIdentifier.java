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
package no.trank.openpipe.lang.step;

import java.nio.charset.Charset;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NullNotEmpty;

/**
 * A pipeline step for identifying langauge of a field.
 * <br/>
 * Detected languages are: <tt>da</tt>, <tt>de</tt>, <tt>en</tt>, <tt>es</tt>, <tt>fr</tt>, <tt>it</tt>, <tt>nl</tt>,
 * <tt>no</tt>, <tt>pt</tt> and <tt>sv</tt>.
 * <br/>
 * If a language cannot be detected, language is set to {@link #getDefaultLang() defaultLang}.
 * <br/>
 * Implementation uses ICU4J's {@link CharsetDetector} for detecting languages.
 *
 * @version $Revision$
 */
public class LanguageIdentifier extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(LanguageIdentifier.class);
   private final CharsetDetector detector = new CharsetDetector();
   private final Charset charset = Charset.forName("ISO-8859-1");
   @NotEmpty
   private String inputField;
   @NotEmpty
   private String langField = "language";
   @NullNotEmpty
   private String defaultLang;
   private boolean overwrite;
   private int minConfidence = 50;

   /**
    * Creates a step with <tt>&quot;LanguageIdentifier&quot;</tt> as name.
    */
   public LanguageIdentifier() {
      detector.setDeclaredEncoding(charset.name());
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final String text = doc.getFieldValue(inputField);
      if (!overwrite && doc.getFieldValue(langField) != null) {
         log.debug("Field '{}' already set to '{}'", langField, doc.getFieldValue(langField));
      } else {
         if (text != null) {
            detector.setText(text.getBytes(charset));
            final CharsetMatch match = detector.detect();
            final int confidence = match.getConfidence();
            if (confidence < minConfidence) {
               log.debug("Confidence {} below minConfidence {}", confidence, minConfidence);
               doc.setFieldValue(langField, defaultLang);
            } else {
               final String lang = match.getLanguage();
               if (lang == null) {
                  log.debug("Confidence: {} but no language detected", confidence);
                  doc.setFieldValue(langField, defaultLang);
               } else {
                  log.debug("Confidence: {} detected language {}", confidence, lang);
                  doc.setFieldValue(langField, lang);
               }
            }
         }
      }
      return PipelineStepStatus.DEFAULT;
   }

   public String getInputField() {
      return inputField;
   }

   public void setInputField(String inputField) {
      this.inputField = inputField;
   }

   /**
    * Gets the field name to put the detected language.
    *
    * @return the field name to put the detected language.
    */
   public String getLangField() {
      return langField;
   }

   /**
    * Sets the field name to put the detected language.
    *
    * @param langField the field name to put the detected language.
    */
   public void setLangField(String langField) {
      this.langField = langField;
   }

   /**
    * Gets the default language, default value is <tt>null</tt>.
    *
    * @return the default language.
    */
   public String getDefaultLang() {
      return defaultLang;
   }

   /**
    * Sets the default language.
    *
    * @param defaultLang the new default language.
    */
   public void setDefaultLang(String defaultLang) {
      this.defaultLang = defaultLang;
   }

   /**
    * Gets whether to overwrite field with name {@link #getLangField() langField} or not.
    *
    * @return <tt>true</tt> if field with name {@link #getLangField() langField} can be overwritten.
    */
   public boolean isOverwrite() {
      return overwrite;
   }

   /**
    * Sets whether to overwrite field with name {@link #getLangField() langField} or not.
    *
    * @param overwrite <tt>true</tt> to overwrite.
    */
   public void setOverwrite(boolean overwrite) {
      this.overwrite = overwrite;
   }

   /**
    * Gets the minimum confidence needed for a detected language. If confidence is <tt>&lt; minConfidence</tt>
    * {@link #getDefaultLang() defaultLang} is used.
    *
    * @return the minimum confidence needed for a detected language.
    */
   public int getMinConfidence() {
      return minConfidence;
   }

   /**
    * Sets the minimum confidence needed for a detected language.
    *
    * @param minConfidence the minimum confidence needed for a detected language.
    */
   public void setMinConfidence(int minConfidence) {
      this.minConfidence = minConfidence;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   @Override
   public String toString() {
      return "LanguageIdentifier{" +
            "inputField='" + inputField + '\'' +
            ", langField='" + langField + '\'' +
            ", defaultLang='" + defaultLang + '\'' +
            ", overwrite=" + overwrite +
            ", minConfidence=" + minConfidence +
            '}';
   }
}
