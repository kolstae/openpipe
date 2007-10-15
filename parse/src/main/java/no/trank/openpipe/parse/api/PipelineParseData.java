package no.trank.openpipe.parse.api;

import java.io.IOException;
import java.io.InputStream;

import no.trank.openpipe.api.document.RawData;

/**

 * @version $Revision$
 */
public class PipelineParseData implements ParseData {
   private final RawData data;
   private final boolean includeProperties;
   private final String fileName;

   public PipelineParseData(RawData data, boolean includeProperties, String fileName) {
      this.data = data;
      this.includeProperties = includeProperties;
      this.fileName = fileName;
   }

   public InputStream getInputStream() throws IOException {
      return data.getInputStream();
   }

   public int getLength() {
      return data.getLength();
   }

   public boolean includeProperties() {
      return includeProperties;
   }

   public String getFileName() {
      return fileName;
   }
}
