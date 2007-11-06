package no.trank.openpipe.api.document;

/**
 * An interface describing a document-producer.
 *
 * @version $Revision$
 */
public interface DocumentProducer extends Iterable<Document> {

   /**
    * Initializes the document-producer. Must be called before the first call to {@link #iterator()}.
    */
   void init();

   /**
    * Closes this document-producer, releasing any resources held.
    */
   void close();

   /**
    * Close this document-producer, and do any error handling.  
    */
   void fail();
}
