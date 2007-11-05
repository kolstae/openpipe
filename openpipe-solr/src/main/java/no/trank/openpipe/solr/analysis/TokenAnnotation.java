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
