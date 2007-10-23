package no.trank.openpipe.solr.analysis;

import no.trank.openpipe.api.document.AnnotatedField;

/**
 * @version $Revision$
 */
public interface TokenSerializer {
   String serialize(AnnotatedField field);
}
