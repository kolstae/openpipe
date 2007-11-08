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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import no.trank.openpipe.api.document.Annotation;

/**
 * @version $Revision$
 */
public class TokenStreamAnnotation {
   private Map<String, List<Annotation>> annotations = new LinkedHashMap<String, List<Annotation>>();
   private final TokenStream stream;

   public TokenStreamAnnotation(TokenStream stream) {
      this.stream = stream;
   }

   public void process() throws IOException {
      try {
         Token tok;
         while ((tok = stream.next()) != null) {
            final List<Annotation> list = getList(tok.type());
            if (tok instanceof Annotation) {
               list.add((Annotation) tok);
            } else {
               list.add(new TokenAnnotation(tok));
            }
         }
      } finally {
         stream.close();
      }
   }

   private List<Annotation> getList(String type) {
      List<Annotation> list = annotations.get(type);
      if (list == null) {
         list = new ArrayList<Annotation>();
         annotations.put(type, list);
      }
      return list;
   }

   public Map<String, List<Annotation>> getAnnotations() {
      return annotations;
   }
}
