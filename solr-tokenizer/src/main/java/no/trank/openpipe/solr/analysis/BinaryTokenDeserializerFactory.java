package no.trank.openpipe.solr.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenizerFactory;

/**
 * @version $Revision$
 */
public class BinaryTokenDeserializerFactory extends BaseTokenizerFactory {

   public TokenStream create(Reader input) {
      return new BinaryTokenDeserializer(input);
   }
}
