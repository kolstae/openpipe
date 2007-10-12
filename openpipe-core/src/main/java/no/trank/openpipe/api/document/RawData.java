package no.trank.openpipe.api.document;

import java.io.IOException;
import java.io.InputStream;

/**
 * @version $Revision:672 $
 */
public interface RawData {   
   InputStream getInputStream() throws IOException;
   int getLength();
   void release();
   boolean isReleased();
}
