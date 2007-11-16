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
package no.trank.openpipe.opennlp.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import opennlp.tools.namefind.NameFinder;
import opennlp.tools.namefind.NameFinderME;

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.BaseAnnotation;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.ResolvedAnnotation;
import no.trank.openpipe.config.annotation.NotEmpty;

/**
 * @version $Revision$
 */
public class ONLPNEDetector extends MultiInputFieldPipelineStep {
   public static final String TYPE_NE = "opennlp.ne.";
   @NotEmpty
   private Map<String, NameFinder> nameFinders = null;

   public ONLPNEDetector() {
      super("Open NLP NE-detector");
   }

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      for (AnnotatedField fieldValue : fieldValues) {
         processField(fieldValue);
      }
   }

   private void processField(AnnotatedField field) {
      final Map<String, NameFinderHolder> holders = buildHolders();
      final Iterator<ResolvedAnnotation> sentIt = field.iterator(ONLPSentenceDetector.TYPE_SENTENCE);
      final ListIterator<ResolvedAnnotation> tokIt = field.iterator(ONLPTokenizer.TYPE_TOKENIZE);
      final ArrayList<String> words = new ArrayList<String>();
      final ArrayList<ResolvedAnnotation> tokens = new ArrayList<ResolvedAnnotation>();
      while (sentIt.hasNext()) {
         if (buildWordsTokens(sentIt.next(), tokIt, words, tokens)) {

            for (Map.Entry<String, NameFinderHolder> entry : holders.entrySet()) {
               findNE(words, tokens, entry.getValue());
            }
         }
      }
      for (Map.Entry<String,NameFinderHolder> entry : holders.entrySet()) {
         final List<Annotation> list = entry.getValue().getAnnotations();
         if (!list.isEmpty()) {
            field.add(TYPE_NE + entry.getKey(), list);
         }
      }
   }

   private static void findNE(ArrayList<String> words, ArrayList<ResolvedAnnotation> tokens, NameFinderHolder holder) {
      final NameFinder nameFinder = holder.getNameFinder();
      final Map<String, String> prevTags = holder.getPreviousTags();
      final List<?> tags = nameFinder.find(words, prevTags);
      Annotation startToken = null;
      final Iterator<String> wordIt = words.iterator();
      for (ListIterator<?> it = tags.listIterator(); it.hasNext();) {
         final String tag = (String) it.next();
         prevTags.put(wordIt.next(), tag);
         if (startToken != null) {
            final boolean startTag = NameFinderME.START.equals(tag);
            if (startTag || NameFinderME.OTHER.equals(tag)) {
               final Annotation endToken = tokens.get(it.previousIndex() - 1);
               holder.getAnnotations().add(new BaseAnnotation(startToken.getStartPos(), endToken.getEndPos()));
               if (startTag) {
                  startToken = endToken;
               } else {
                  startToken = null;
               }
            }
         } else if (NameFinderME.START.equals(tag)) {
            startToken = tokens.get(it.nextIndex() - 1);
         }
      }
   }

   private static boolean buildWordsTokens(ResolvedAnnotation sentence, ListIterator<ResolvedAnnotation> tokIt,
         ArrayList<String> words, ArrayList<ResolvedAnnotation> tokens) {
      words.clear();
      tokens.clear();
      ResolvedAnnotation tok = tokIt.next();
      while (tokIt.hasNext() && tok.getStartPos() < sentence.getEndPos()) {
         words.add(tok.getValue());
         tokens.add(tok);
         tok = tokIt.next();
      }
      if (tokIt.hasNext()) {
         tokIt.previous();
      }
      return !words.isEmpty();
   }

   private Map<String, NameFinderHolder> buildHolders() {
      final Map<String, NameFinderHolder> holderMap = new HashMap<String, NameFinderHolder>();
      for (Map.Entry<String,NameFinder> e : nameFinders.entrySet()) {
         holderMap.put(e.getKey(), new NameFinderHolder(e.getValue()));
      }
      return holderMap;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public Map<String, NameFinder> getNameFinders() {
      return nameFinders;
   }

   public void setNameFinders(Map<String, NameFinder> nameFinders) {
      this.nameFinders = nameFinders;
   }

   private static final class NameFinderHolder {
      private final NameFinder nameFinder;
      private final List<Annotation> annotations = new ArrayList<Annotation>();
      private final Map<String, String> previousTags = new HashMap<String, String>();

      public NameFinderHolder(NameFinder nameFinder) {
         this.nameFinder = nameFinder;
      }

      public NameFinder getNameFinder() {
         return nameFinder;
      }

      public List<Annotation> getAnnotations() {
         return annotations;
      }

      public Map<String, String> getPreviousTags() {
         return previousTags;
      }
   }
}