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
package no.trank.openpipe.solr.analysis;

import org.apache.lucene.analysis.Token;

import no.trank.openpipe.api.document.ResolvedAnnotation;

/**
 * @version $Revision$
 */
public class TokenAnnotation extends Token implements ResolvedAnnotation, Cloneable {

   public TokenAnnotation(Token token) {
      super(token.termText(), token.startOffset(), token.endOffset(), token.type());
      setPositionIncrement(token.getPositionIncrement());
   }

   @Override
   public String getValue() {
      return termText();
   }

   @Override
   public int getStartPos() {
      return startOffset();
   }

   @Override
   public int getEndPos() {
      return endOffset();
   }

   @Override
   public Object clone() {
      return super.clone();
   }
}
