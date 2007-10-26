package no.trank.openpipe.solr.analysis;

import java.io.Closeable;

import no.trank.openpipe.api.document.AnnotatedField;

/**
 * @version $Revision$
 */
public interface TokenSerializer extends Closeable {
   String serialize(AnnotatedField field);
}
