package no.trank.openpipe.solr.analysis.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @version $Revision$
 */
public class Base64OutputStream extends Base64Output {
   private final OutputStream out;
   private final byte[] buf;
   private int pos;

   /**
    * Creates a <tt>Base64OutputStream</tt> with a buffer size of <tt>4096</tt>.
    * 
    * @param out the outputStream to write to.
    */
   public Base64OutputStream(OutputStream out) {
      this(out, 4096);
   }

   /**
    * Creates a <tt>Base64OutputStream</tt> with the given buffer size.
    *
    * @param out the outputStream to write to.
    * @param bufferSize the buffer size for this stream.
    */
   public Base64OutputStream(OutputStream out, int bufferSize) {
      if (out == null) {
         throw new NullPointerException("OutputStream == null");
      }
      if (bufferSize < 1) {
         throw new IllegalArgumentException("bufferSize must be > 0 was" + bufferSize);
      }
      this.out = out;
      buf = new byte[bufferSize];
   }

   @Override
   public void write(final int b) throws IOException {
      writeBase64(b);
   }

   @Override
   public void write(final byte b[], final int off, final int len) throws IOException {
      writeBase64(b, off, len);
   }

   @Override
   protected void writeByte(char b) throws IOException {
      if (pos >= buf.length) {
         out.write(buf);
         pos = 0;
      }
      buf[pos++] = (byte) b;
   }

   @Override
   public void flush() throws IOException {
      flushBase64();
      if (pos > 0) {
         out.write(buf, 0, pos);
         pos = 0;
      }
      out.flush();
   }

   @Override
   public void close() throws IOException {
      try {
         super.close();
      } finally {
         out.close();
      }
   }
}