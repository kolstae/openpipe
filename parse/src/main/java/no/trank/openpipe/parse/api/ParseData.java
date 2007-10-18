package no.trank.openpipe.parse.api;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface representing data to be parsed.
 * 
 * @version $Revision$
 */
public interface ParseData {

   /**
    * Gets a stream for the data.
    * 
    * @return a stream for the data.
    * 
    * @throws IOException if an I/O-error occured.
    */
   InputStream getInputStream() throws IOException;

   /**
    * Gets the length of the stream returned by {@link #getInputStream()}.
    * 
    * @return the number of bytes.
    */
   int getLength();

   /**
    * Tells whether properties of a parsed document is of interest.
    * 
    * @return <tt>true</tt> if properties of a document is of interest, otherwise <tt>false</tt>.
    */
   boolean includeProperties();

   /**
    * Gets the filename of the data.
    * 
    * @return the filename of the data.
    */
   String getFileName();
}
