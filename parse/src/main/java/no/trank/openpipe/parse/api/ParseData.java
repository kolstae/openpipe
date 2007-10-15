package no.trank.openpipe.parse.api;

import java.io.IOException;
import java.io.InputStream;

/**
 * @version $Revision$
 */
public interface ParseData {

   InputStream getInputStream() throws IOException;

   int getLength();

   boolean includeProperties();

   String getFileName();
}
