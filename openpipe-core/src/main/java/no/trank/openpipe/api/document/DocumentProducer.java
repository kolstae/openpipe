package no.trank.openpipe.api.document;

/**
 * @version $Revision: 874 $
 */
public interface DocumentProducer extends Iterable<Document> {
   void init();
   void close();
}
