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
