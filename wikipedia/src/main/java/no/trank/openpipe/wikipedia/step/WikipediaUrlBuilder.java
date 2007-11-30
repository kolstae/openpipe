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

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;
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
   private String titleField = "title";
   private String urlField = "url";

   public WikipediaUrlBuilder() {
      super("WikipediaUrlBuilder");
   }

   public PipelineStepStatus execute(Document doc) throws PipelineException {
      String title = doc.getFieldValue(titleField);
      String url = baseUrl + convertTitle(title);
      doc.setFieldValue(urlField, url);
      return PipelineStepStatus.DEFAULT;
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

   private String convertTitle(String title) {
      return title.replace(' ', '_');
   }

   public String getRevision() {
      return "$Revision$";
   }
}
