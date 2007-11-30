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

import java.util.regex.Pattern;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotNull;
import no.trank.openpipe.config.annotation.NullNotEmpty;

/**
 * Extracts a short text from the wikipedia articel to use as intro. This class was developed by trial and error, so
 * don't expect it to be exact in all cases.
 *
 * A document body that starts with "#REDIRECT" will be unchanged. (No intro will be set) 
 *
 * @version $Revision$
 */
public class WikipediaIntroExtractor extends BasePipelineStep {
   private static final Pattern BOLD_ITALIC = Pattern.compile("'''?([^']*)'''?");
   private static final Pattern LINK = Pattern.compile("\\[\\[(?:[^\\]]*\\|)*([^\\]|]*)\\]\\]");
   private static final Pattern LAST_WORD = Pattern.compile("(\\s?\\S+\\s*$)");

   @NotNull
   private String bodyField;
   @NotNull
   private String introField;
   @NullNotEmpty
   private String relativeSizeField;
   private int introSize = 160;


   public WikipediaIntroExtractor() {
      super("WikipediaIntroExtractor");
   }

   public PipelineStepStatus execute(Document doc) throws PipelineException {
      String bodyText = doc.getFieldValue(bodyField);
      if (!bodyText.startsWith("#REDIRECT")) {
         int relIntroSize = relativeSizeField != null && doc.getFieldValue(relativeSizeField) != null ? introSize - doc.getFieldValue(relativeSizeField).length() : introSize;
         doc.addFieldValue(introField, getIntro(bodyText, relIntroSize));
      }
      return PipelineStepStatus.DEFAULT;
   }

   public String getIntro(String body, int introSize) {
      StringBuilder introBuilder = new StringBuilder();
      MediaWikiStripper wikiStripper = new MediaWikiStripper(body);
      while (introBuilder.length() < introSize) {
         introBuilder.append(wikiStripper.nextChunk());
      }
      String intro = introBuilder.toString();
      do {
         intro = LAST_WORD.matcher(intro).replaceAll("...");
      } while (intro.length() > introSize);
      return intro;
   }

   public String getRevision() {
      return "$Revision$";
   }

   /**
    * Gets the field that contains the wikipedia body text.
    *
    * @return the field that contains the wikipedia body text.
    */
   public String getBodyField() {
      return bodyField;
   }

   /**
    * Sets the field that contains the wikipedia body text.
    *
    * @param bodyField the field that contains the wikipedia body text.
    */
   public void setBodyField(String bodyField) {
      this.bodyField = bodyField;
   }

   /**
    * Gets the intro output field.
    *
    * @return the intro output field.
    */
   public String getIntroField() {
      return introField;
   }

   /**
    * Sets the intro output field.
    *
    * @param introField the intro output field.
    */
   public void setIntroField(String introField) {
      this.introField = introField;
   }

   /**
    * Gets the intro size in characters.
    *
    * @return the intro size in characters.
    */
   public int getIntroSize() {
      return introSize;
   }

   /**
    * Sets the intro size in characters.
    *
    * @param introSize the intro size in characters.
    */
   public void setIntroSize(int introSize) {
      this.introSize = introSize;
   }

   /**
    * Gets the relative sizing fieldname.
    *
    * @return the relative sizing fieldname.
    * @see #setRelativeSizeField(String)
    */
   public String getRelativeSizeField() {
      return relativeSizeField;
   }

   /**
    * Sets the relative sizing fieldname.
    * <p/>
    * If you set this field, the size of the intro will be reduced to fit the content of this field into it.
    *
    * @param relativeSizeField the relative sizing fieldname.
    */
   public void setRelativeSizeField(String relativeSizeField) {
      this.relativeSizeField = relativeSizeField;
   }

   private class MediaWikiStripper {
      private String text;
      private int currentPos;
      private boolean startOfLine = true;
      private int curlyCount = 0;

      public MediaWikiStripper(String text) {
         this.text = text;
      }

      public String nextChunk() {
         StringBuilder rawChunk = rawChunk();
         String tmpChunk = BOLD_ITALIC.matcher(rawChunk).replaceAll("$1");
         tmpChunk = LINK.matcher(tmpChunk).replaceAll("$1");
         return tmpChunk;
      }

      private StringBuilder rawChunk() {
         if (startOfLine && text.charAt(currentPos) == ':') {
            skipLine();
            return rawChunk();
         }
         if (text.charAt(currentPos) == '{' && currentPos + 2 < text.length() && text.charAt(currentPos + 1) == '{') {
            curlyCount++;
            currentPos += 2;
            skipToEndCulies();
            return rawChunk();
         }
         startOfLine = false;
         StringBuilder chunk = new StringBuilder();
         while (currentPos < text.length()) {
            chunk.append(text.charAt(currentPos));
            if (text.charAt(currentPos) == '\n') {
               startOfLine = true;
               currentPos++;
               if (chunk.length() == 1 && chunk.charAt(0) == '\n') {
                  return rawChunk();
               } else {
                  return chunk;
               }
            } else if (text.charAt(currentPos) == ' ' || text.charAt(currentPos) == '.') {
               currentPos++;
               return chunk;
            }
            currentPos++;
         }
         return chunk;
      }

      private void skipToEndCulies() {
         while (curlyCount > 0 && currentPos < text.length()) {
            if (text.charAt(currentPos) == '{' && currentPos + 2 < text.length() && text.charAt(currentPos + 1) == '{') {
               curlyCount++;
               currentPos += 2;
            } else
            if (text.charAt(currentPos) == '}' && currentPos + 2 < text.length() && text.charAt(currentPos + 1) == '}') {
               curlyCount--;
               currentPos += 2;
            } else {
               currentPos++;
            }
         }
      }

      private void skipLine() {
         currentPos = text.indexOf('\n', currentPos);
      }
   }
}
