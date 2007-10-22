package no.trank.openpipe.solr.analysis;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Set;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.ResolvedAnnotation;

/**
 * @version $Revision$
 */
public class AnnotationTokenStream extends TokenStream {
   private ResolvedAnnotation[] anns;
   private ListIterator<ResolvedAnnotation>[] iterators;
   private String[] types;
   private int lastEnd = -1;

   @SuppressWarnings({"unchecked"})
   public AnnotationTokenStream(AnnotatedField doc, Set<String> annotations) {
      types = annotations.toArray(new String[annotations.size()]);
      anns = new ResolvedAnnotation[annotations.size()];
      iterators = new ListIterator[annotations.size()];
      for (int i = 0; i < types.length; i++) {
         final ListIterator<ResolvedAnnotation> it = doc.iterator(types[i]);
         iterators[i] = it;
         anns[i] = nextOrNull(it);
      }
   }

   @Override
   public Token next() throws IOException {
      int idx = 0;
      ResolvedAnnotation ann = anns[idx];
      for (int i = 1; i < anns.length; i++) {
         final ResolvedAnnotation a2 = anns[i];
         if (a2 != null && (ann == null || a2.getStartPos() < ann.getStartPos() ||
               a2.getStartPos() == ann.getStartPos() && a2.getEndPos() < ann.getEndPos())) {
            ann = a2;
            idx = i;
         }
      }
      if (ann != null) {
         anns[idx] = nextOrNull(iterators[idx]);
         final boolean noIncr = lastEnd == ann.getEndPos();
         lastEnd = ann.getEndPos();
         if (ann instanceof Token) {
            return (Token) ann;
         }
         final Token token = new Token(ann.getValue(), ann.getStartPos(), lastEnd, types[idx]);
         if (noIncr) {
            token.setPositionIncrement(0);
         }
         return token;
      }
      return null;
   }

   private static ResolvedAnnotation nextOrNull(ListIterator<ResolvedAnnotation> it) {
      return it.hasNext() ? it.next() : null;
   }
   
}
