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
package no.trank.openpipe.wikipedia.step;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;

/**
 * Makes a wikipedia url from the title.
 * <br/><br/>
 * Prerequisite: Titlefield must be set.
 * <br/><br/>
 * If baseUrl is set to http://en.wikipedia.org/wiki/, the url for the page with title: Hjalmar Johansen will
 * become: http://en.wikipedia.org/wiki/Hjalmar_Johansen
 *
 * @version $Revision$
 */
public class WikipediaUrlBuilder extends BasePipelineStep {
   @NotNull
   private String baseUrl;
   @NotEmpty
   private String titleField = "title";
   @NotEmpty
   private String urlField = "url";
   @NotEmpty
   private String urlEncoding = "UTF-8";

   @Override
   public void prepare() throws PipelineException {
      super.prepare();
      Charset.forName(urlEncoding);
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      try {
         final String title = doc.getFieldValue(titleField);
         if (title != null) {
            doc.setFieldValue(urlField, buildUrl(title));
         }
         return PipelineStepStatus.DEFAULT;
      } catch (UnsupportedEncodingException e) {
         // Should never happen since this has been checked in prepare
         throw new PipelineException("Unsupported urlEncoding", e);
      }
   }

   private String buildUrl(String title) throws UnsupportedEncodingException {
      int startIdx = 0;
      final StringBuilder sb = new StringBuilder(title.length() + 16 + baseUrl.length());
      sb.append(baseUrl);
      for (int i = 0; i < title.length(); i++) {
         final char c = title.charAt(i);
         if (c == '/') {
            if (startIdx < i) {
               sb.append(URLEncoder.encode(title.substring(startIdx, i), urlEncoding));
            }
            sb.append(c);
            startIdx = i + 1;
         } else if (c == ' ') {
            if (startIdx < i) {
               sb.append(URLEncoder.encode(title.substring(startIdx, i), urlEncoding));
            }
            sb.append('_');
            startIdx = i + 1;
         }
      }
      if (startIdx < title.length()) {
         sb.append(URLEncoder.encode(title.substring(startIdx), urlEncoding));
      }
      return sb.toString();
   }

   /**
    * Gets the url encoding to use for the created url.
    *
    * @return the url encoding to use for the created url.
    */
   public String getUrlEncoding() {
      return urlEncoding;
   }

   /**
    * Sets the url encoding to use for the created url.
    *
    * @param urlEncoding the url encoding to use for the created url.
    */
   public void setUrlEncoding(String urlEncoding) {
      this.urlEncoding = urlEncoding;
   }

   /**
    * Gets the base url to the wikipedia site.
    *
    * @return the base url to the wikipedia site
    */
   public String getBaseUrl() {
      return baseUrl;
   }

   /**
    * Sets the base url to the wikipedia site
    * @param baseUrl the base url to the wikipedia site.
    */
   public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
   }

   /**
    * Gets the fieldName for the title of the document.
    *
    * @return the fieldName for the title of the document
    */
   public String getTitleField() {
      return titleField;
   }

   /**
    * Sets the fieldName for the title of the document.
    *
    * @param titleField the fieldName for the title of the document
    */
   public void setTitleField(String titleField) {
      this.titleField = titleField;
   }

   /**
    * Gets the field name of the field that the url should be set.
    *
    * @return the field name of the field that the url should be set
    */
   public String getUrlField() {
      return urlField;
   }

   /**
    * Sets the field name of the field that the url should be set.
    *
    * @param urlField the field name of the field that the url should be set
    */
   public void setUrlField(String urlField) {
      this.urlField = urlField;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }
}
