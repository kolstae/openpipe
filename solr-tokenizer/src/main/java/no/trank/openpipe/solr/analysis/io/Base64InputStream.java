package no.trank.openpipe.solr.analysis.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @version $Revision$
 */
public class Base64InputStream extends InputStream {
   private static final int[] B64_VALS = new int['z' + 1];
   private static final int MASK = 0x3f;
   private final Reader in;
   private int off = 8;
   private int rest;

   public Base64InputStream(Reader in) {
      this.in = in;
   }

   public Base64InputStream(InputStream in) {
      this(new Base64InputStreamReader(in));
   }

   @Override
   public int read() throws IOException {
      if (off > 6) {
         final int b = readDecoded();
         if (b < 0) {
            return -1;
         }
         rest = b << 2;
         off = 2;
      }
      final int b = readDecoded();
      if (b < 0) {
         return -1;
      }
      final int result = rest | (b >> (6 - off));
      rest = ((b << off) & MASK) << 2;
      off += 2;
      return result;
   }

   private int readDecoded() throws IOException {
      final int b = in.read();
      if (b >= 0 && b < B64_VALS.length) {
         return B64_VALS[b];
      }
      return -1;
   }

   @Override
   public long skip(final long n) throws IOException {
      long skipped = 0;
      while (skipped < n && read() >= 0) {
         skipped++;
      }
      return skipped;
   }

   @Override
   public void close() throws IOException {
      in.close();
   }
   
   static {
      for (int i = 0; i < B64_VALS.length; i++) {
         B64_VALS[i] = -1;
      }
      for (int i = 0; i < Base64OutputStream.B64_CHARS.length; i++) {
         B64_VALS[Base64OutputStream.B64_CHARS[i]] = i;
      }
   }

   private static class Base64InputStreamReader extends Reader {
      private final InputStream in;

      public Base64InputStreamReader(InputStream in) {
         this.in = in;
      }

      @Override
      public int read() throws IOException {
         return in.read();
      }

      @Override
      public int read(char cbuf[], int off, int len) throws IOException {
         throw new UnsupportedOperationException();
      }

      @Override
      public void close() throws IOException {
         in.close();
      }
   }
}