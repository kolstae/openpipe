package no.trank.openpipe.api.document;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface representing the raw-data of a document.
 *
 * @version $Revision$
 */
public interface RawData {

   /**
    * Gets a stream of the data.
    *
    * @return a stream of the data.
    *
    * @throws IOException if an I/O-error occured.
    */
   InputStream getInputStream() throws IOException;

   /**
    * Gets the length of the data.
    * 
    * @return the length of the data.
    */
   int getLength();

   /**
    * Releases the resources of this instance.
    */
   void release();

   /**
    * Gets whether {@link #release()} has been called for this instance.
    * 
    * @return <tt>true</tt> if {@link #release()} has been called for this instance, otherwise <tt>false</tt>.
    */
   boolean isReleased();
}
