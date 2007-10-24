package no.trank.openpipe.solr.analysis.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @version $Revision$
 */
public class Base64OutputStream extends OutputStream {
   protected static final char[] B64_CHARS = new char[]{
         'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
         'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
         'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
         'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
   private static final int BUFFER_INC = 4096;
   private static final int MASK = 0x3f;
   private char[] buf;
   private int off = 2;
   private int pos;
   private int rest;

   /**
    * Constructs a <tt>Base64OutputStream</tt> with default capacity: <tt>4096</tt>.
    */
   public Base64OutputStream() {
      this(BUFFER_INC);
   }

   /**
    * Constructs a <tt>Base64OutputStream</tt> with the given capacity.
    * 
    * @param capacity the capacity for this stream.
    */
   public Base64OutputStream(int capacity) {
      buf = new char[capacity];
   }

   @Override
   public void write(final int b) throws IOException {
      ensureCap(pos + 2);
      writeFast(b);
   }

   @Override
   public void write(final byte b[], final int off, final int len) throws IOException {
      ensureCap(pos + len);
      for (int i = off; i < len; i++) {
         writeFast(b[i] & 0xff);
      }
   }

   private void writeFast(int b) {
      buf[pos++] = B64_CHARS[rest | b >>> off & MASK];
      if (off > 4) {
         buf[pos++] = B64_CHARS[b & MASK];
         off = 2;
         rest = 0;
      } else {
         rest = b << 6 - off & MASK;
         off += 2;
      }
   }

   private void ensureCap(int capacity) {
      if (capacity >= buf.length) {
         final char[] tmp = new char[Math.max(capacity, buf.length + BUFFER_INC)];
         System.arraycopy(buf, 0, tmp, 0, pos);
         buf = tmp;
      }
   }

   @Override
   public void flush() throws IOException {
      if (off > 2) {
         ensureCap(pos + (off >> 1) - 1);
         buf[pos++] = B64_CHARS[rest];
         while (off < 8) {
            buf[pos++] = (byte) '=';
            off += 2;
         }
      }
   }

   @Override
   public void close() throws IOException {
      flush();
   }

   @Override
   public String toString() {
      return new String(buf, 0, pos);
   }
   
   public void reset() {
      pos = 0;
      off = 2;
      rest = 0;
   }

   public void trimToSize(int maxSize) {
      if (buf.length > maxSize) {
         buf = new char[maxSize];
      }
   }
}
