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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.BaseAnnotatedField;
import no.trank.openpipe.api.document.Document;

/**
 * Chops field(s) to a maximum length.
 *
 * Set the appendOnChop, if you want this step to append with a string to indicate that it has been chopped.
 * {@link #setAppendOnChop(String)}
 *
 * @version $Revision$
 */
public class ChopField extends MultiInputFieldPipelineStep {
   private int chopLength;
   private String appendOnChop;
   private String fitField;
   private Pattern LAST_WORD = Pattern.compile("[\\s\\.]+[\\S&&[^\\.]]+[\\s\\.]*$");

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      List<AnnotatedField> newFields = new ArrayList<AnnotatedField>();
      for (AnnotatedField fieldValue : fieldValues) {
         if (fieldValue.getValue().length() > chopLength) {
            String choppedString = chop(fieldValue.getValue(), doc.getFieldValue(fitField));
            newFields.add(new BaseAnnotatedField(choppedString));
         } else {
            newFields.add(fieldValue);
         }
      }
      doc.setField(fieldName, newFields);
   }

   private String chop(String value, String fitFieldValue) {
      int chopTo = appendOnChop == null ? chopLength : chopLength - appendOnChop.length();
      chopTo = fitFieldValue == null ? chopTo : chopTo - fitFieldValue.length();
      chopTo = Math.max(0, chopTo);
      Matcher matcher = LAST_WORD.matcher(value.substring(0, chopTo));
      return matcher.replaceAll(appendOnChop == null ? "" : appendOnChop);
   }

   /**
    * Gets the maximum length that the field(s) should be chopped to.
    *
    * @return the maximum length that the field(s) should be chopped to.
    */
   public int getChopLength() {
      return chopLength;
   }

   /**
    * Sets the the maximum length that the field(s) should be chopped to.
    *
    * @param chopLength the maximum length that the field(s) should be chopped to.
    */
   public void setChopLength(int chopLength) {
      this.chopLength = chopLength;
   }

   /**
    * Sets the string that should be appended to the string if it has been chopped.
    *
    * @return the string that should be appended to the string if it has been chopped.
    */
   public String getAppendOnChop() {
      return appendOnChop;
   }

   /**
    * Gets the string that should be appended to the string if it has been chopped.
    *
    * @param appendOnChop the string that should be appended to the string if it has been chopped.
    */
   public void setAppendOnChop(String appendOnChop) {
      this.appendOnChop = appendOnChop;
   }

   /**
    * Gets the fitField.
    *
    * @return the fitField
    * @see #setFitField(String)
    */
   public String getFitField() {
      return fitField;
   }

   /**
    * Sets the fitField. If this is set the chopper will chop texts relative to the length of the value of fitfield.
    * That means that the chopLength will be shortened by the length of the fitField value.
    *
    * @param fitField the fitField
    */
   public void setFitField(String fitField) {
      this.fitField = fitField;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }
}
