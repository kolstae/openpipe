package no.trank.openpipe.solr;

import java.io.IOException;
import java.io.InputStream;

/**
 * @version $Revision$
 */
public class XmlInputStream extends InputStream {
   private boolean skipping;
   private InputStream inputStream;
   
   public XmlInputStream(InputStream inputStream) {
      this.inputStream = inputStream; 
      skipping = true;
   }

   @Override
   public int read() throws IOException {
      int ret = inputStream.read();
      
      while(skipping && ret >= 0) {
         if(ret != ' ' && ret != '\t' && ret != '\n') {
            skipping = false;
         }
         else {
            ret = inputStream.read();
         }
      }
      
      if(ret < 0) {
         skipping = false;
      }
      
      return ret;
   }
   
   @Override
   public int read(byte b[], int off, int len) throws IOException {
      boolean one = false;
      
      while(skipping) {
         int tmp = read();
         if(!skipping) {
            b[off++] = (byte)tmp;
            --len;
            one = true;
         }
      }
      
      int ret = inputStream.read(b, off, len);
      
      if(ret == -1) {
         return one ? 1 : -1;
      }
      else {
         return one ? ret + 1 : ret;
      }
   }
   
   @Override
   public long skip(long n) throws IOException {
      long ret = 0L;
      while(--n >= 0) {
         if(read() >= 0) {
            ++ret;
         }
         else {
            break;
         }
      }
      
      return ret;
   }
   
   @Override
   public int available() throws IOException {
      return inputStream.available();
   }

   @Override
   public void close() throws IOException {
      inputStream.close();
   }
}
