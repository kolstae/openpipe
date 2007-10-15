package no.trank.openpipe.api.document;

/**
 * @version $Revision$
 */
public interface DocumentProducer extends Iterable<Document> {
   void init();
   void close();
}
