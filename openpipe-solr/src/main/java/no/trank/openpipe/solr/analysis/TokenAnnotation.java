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

   public String getValue() {
      return termText();
   }

   public int getStartPos() {
      return startOffset();
   }

   public int getEndPos() {
      return endOffset();
   }

   @Override
   public Object clone() {
      return super.clone();
   }
}
