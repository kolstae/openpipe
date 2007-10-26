package no.trank.openpipe.solr.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/**
 * Utility-class for I/O operations.
 * 
 * @version $Revision$
 */
public class IOUtil {

   /**
    * Writes a string as <tt>modified UTF-8</tt> as specified by {@link java.io.DataOutput#writeUTF(String)}, 
    * except it writes the length using {@link #writeNibble(OutputStream, int)}, that way strings may be up to 
    * {@link Integer#MAX_VALUE} in length.
    * 
    * @param out the stream to write to.
    * @param str the string to write.
    * 
    * @return the number of bytes written.
    * 
    * @throws IOException if an I/O error occures.
    * 
    * @see #readUTF(InputStream)
    */
   public static int writeUTF(final OutputStream out, final String str) throws IOException {
      final int strlen = str.length();

      // use charAt instead of copying String to char array
      final int utflen = findUtfLen(str, strlen);

      final int lenBytes = writeNibble(out, utflen);

      final byte[] buf = new byte[utflen];
      int i;
      for (i = 0; i < strlen; i++) {
         final int c = str.charAt(i);
         if (!((c >= 0x0001) && (c <= 0x007F))) {
            break;
         }
         buf[i] = (byte) c;
      }

      int idx = i;
      for (; i < strlen; i++) {
         final int c = str.charAt(i);
         if ((c >= 0x0001) && (c <= 0x007F)) {
            buf[idx++] = (byte) c;
         } else if (c > 0x07FF) {
            buf[idx++] = (byte) (0xE0 | c >> 12 & 0x0F);
            buf[idx++] = (byte) (0x80 | c >> 6 & 0x3F);
            buf[idx++] = (byte) (0x80 | c & 0x3F);
         } else {
            buf[idx++] = (byte) (0xC0 | c >> 6 & 0x1F);
            buf[idx++] = (byte) (0x80 | c & 0x3F);
         }
      }
      out.write(buf, 0, utflen);
      return utflen + lenBytes;
   }

   private static int findUtfLen(final String str, final int strlen) {
      int utflen = strlen;
      for (int i = 0; i < strlen; i++) {
         final int c = str.charAt(i);
         if (c < 0x0001 || c > 0x007F) {
            utflen += c > 0x07FF ? 2 : 1;
         }
      }
      return utflen;
   }

   /**
    * Reads a string as <tt>modified UTF-8</tt> as specified by {@link java.io.DataInput#readUTF()}, except it reads the 
    * length using {@link #readUTF(InputStream)}, that way strings may be up to {@link Integer#MAX_VALUE} in length.
    * 
    * @param in the stream to read from.
    * 
    * @return the string read.
    * 
    * @throws IOException if an I/O error occures.
    * @throws EOFException if a end-of-stream was reached.
    * 
    * @see #writeUTF(OutputStream, String) 
    */
   @SuppressWarnings({"OverlyLongMethod"})
   public static String readUTF(InputStream in) throws IOException {
      final int utflen = readNibble(in);
      if (utflen < 0) {
         throw new EOFException();
      }
      final byte[] bytes = new byte[utflen];

      readFully(in, bytes, 0, utflen);

      final char[] chars = new char[utflen];
      int bIdx = readSingleByte(utflen, bytes, chars);

      int count = bIdx;
      while (bIdx < utflen) {
         final int c = bytes[bIdx++] & 0xff;
         switch (c >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
               /* 0xxxxxxx*/
               chars[count++] = (char) c;
               break;
            case 12:
            case 13:
               /* 110x xxxx   10xx xxxx*/
               if (bIdx >= utflen) {
                  throw new UTFDataFormatException("malformed input: partial character at end");
               }
               final int c2 = (int) bytes[bIdx++];
               if ((c2 & 0xC0) != 0x80) {
                  throw new UTFDataFormatException("malformed input around byte " + (bIdx - 1));
               }
               chars[count++] = (char) ((c & 0x1F) << 6 | c2 & 0x3F);
               break;
            case 14:
               /* 1110 xxxx  10xx xxxx  10xx xxxx */
               if (bIdx + 2 > utflen) {
                  throw new UTFDataFormatException("malformed input: partial character at end");
               }
               final int c3 = bytes[bIdx++] & 0xff;
               final int c4 = bytes[bIdx++] & 0xff;
               if ((c3 & 0xC0) != 0x80 || (c4 & 0xC0) != 0x80) {
                  throw new UTFDataFormatException("malformed input around byte " + (bIdx - 2));
               }
               chars[count++] = (char) ((c & 0x0F) << 12 | (c3 & 0x3F) << 6 | c4 & 0x3F);
               break;
            default:
               /* 10xx xxxx,  1111 xxxx */
               throw new UTFDataFormatException("malformed input around byte " + (bIdx - 1));
         }
      }
      // The number of chars produced may be less than utflen
      return new String(chars, 0, count);
   }

   private static int readSingleByte(final int utflen, final byte[] bytes, final char[] chars) {
      int bIdx = 0;
      while (bIdx < utflen) {
         final int c = (int) bytes[bIdx] & 0xff;
         if (c > 127) {
            break;
         }
         chars[bIdx++] = (char) c;
      }
      return bIdx;
   }

   /**
    * Reads the given number of bytes from a stream. 
    * 
    * @param in the stream to read from.
    * @param b the buffer into which the data is read.
    * @param off an int specifying the offset into the data.
    * @param len an int specifying the number of bytes to read.
    * 
    * @throws IOException if an I/O error occures.
    * @throws EOFException if this stream reaches the end before reading all the bytes.
    * 
    * @see java.io.DataInput#readFully(byte[], int, int)
    */
   public static void readFully(final InputStream in, final byte b[], final int off, final int len) throws IOException {
      if (len < 0) {
         throw new IndexOutOfBoundsException();
      }
      int n = 0;
      while (n < len) {
         int count = in.read(b, off + n, len - n);
         if (count < 0) {
            throw new EOFException();
         }
         n += count;
      }
   }

   /**
    * Writes a positive integer with variable length to a stream.
    * <p/>
    * <a href="http://en.wikipedia.org/wiki/Nibble">Nibble</a> is not the correct term for what this writes, but oh well
    * <br/>
    * Integers are written as 7-bit ints with the 8<sup>th</sup> bit being continuation flag. Number of bytes used to 
    * code an integer is <tt>[1-5]</tt>.<br/>
    * E.g.
    * <table>
    *    <tr><th>decimal</th><th>hexadecimal</th><th>encoded bytes</th></tr>
    *    <tr><td><tt>0</tt></td><td><tt>0x00</tt></td><td><tt>0x80</tt></td></tr>
    *    <tr><td><tt>128</tt></td><td><tt>0x80</tt></td><td><tt>0x01 0x80</tt></td></tr>
    *    <tr><td><tt>5964</tt></td><td><tt>0x174C</tt></td><td><tt>0x2E 0xCC</tt></td></tr>
    *    <tr><td><tt>2147483647</tt></td><td><tt>0x7FFFFFFF</tt></td><td><tt>0x07 0x7F 0x7F 0x7F 0xFF</tt></td></tr>
    * </table>
    * 
    * @param out the stream to write to.
    * @param x the positive integer to write.
    * 
    * @return the number of bytes written.
    * 
    * @throws IOException if an I/O error occures.
    * 
    * @see #readNibble(InputStream)
    */
   public static int writeNibble(final OutputStream out, final int x) throws IOException {
      if (x < 0) {
         throw new IOException("The argument " + x + " is negative");
      }
      if (x == 0) {
         out.write(0x80);
         return 1;
      }
      final int len = msb(x) / 7;
      int h = len;
      do {
         if (h == 0) {
            out.write(0x80 | ((x >> (h * 7)) & 0x7f));
         } else {
            out.write(((x >> (h * 7)) & 0x7f));
         }
      } while (h-- != 0);
      return len + 1;
   }

   /**
    * Reads a positive integer with variable length from a stream.
    * <p/>
    * For coding description see {@link #writeNibble(OutputStream, int)}.
    * 
    * @param in the stream to read from.
    * 
    * @return the positive integer read from the stream or <tt>-1</tt> if end of stream was reached before the int could 
    * be decoded.
    * 
    * @throws IOException if an I/O error occures.
    * 
    * @see #writeNibble(OutputStream, int)
    */
   public static int readNibble(final InputStream in) throws IOException {
      int x = 0;
      int b;
      do {
         x <<= 7;
         b = in.read();
         x |= b & 0x7f;
      } while ((b & 0x80) == 0 && b >= 0 && x >= 0);

      if (x < 0) {
         throw new IOException("Invalid nibble read");
      }
      if (b < 0) {
         return -1;
      }

      return x;
   }

   private static int msb(int x) {
      return
            (x < 1 << 15 ?
                  (x < 1 << 7 ?
                        (x < 1 << 3 ?
                              (x < 1 << 1 ?
                                    (x < 1 ?
                                          x < 0 ? 31 : -1 /* 6 */
                                          :
                                          0 /* 5 */
                                    )
                                    :
                                    (x < 1 << 2 ?
                                          1 /* 5 */
                                          :
                                          2 /* 5 */
                                    )
                              )
                              :
                              (x < 1 << 5 ?
                                    (x < 1 << 4 ?
                                          3 /* 5 */
                                          :
                                          4 /* 5 */
                                    )
                                    :
                                    (x < 1 << 6 ?
                                          5 /* 5 */
                                          :
                                          6 /* 5 */
                                    )
                              )
                        )
                        :
                        (x < 1 << 11 ?
                              (x < 1 << 9 ?
                                    (x < 1 << 8 ?
                                          7 /* 5 */
                                          :
                                          8 /* 5 */
                                    )
                                    :
                                    (x < 1 << 10 ?
                                          9 /* 5 */
                                          :
                                          10 /* 5 */
                                    )
                              )
                              :
                              (x < 1 << 13 ?
                                    (x < 1 << 12 ?
                                          11 /* 5 */
                                          :
                                          12 /* 5 */
                                    )
                                    :
                                    (x < 1 << 14 ?
                                          13 /* 5 */
                                          :
                                          14 /* 5 */
                                    )
                              )
                        )
                  )
                  :
                  (x < 1 << 23 ?
                        (x < 1 << 19 ?
                              (x < 1 << 17 ?
                                    (x < 1 << 16 ?
                                          15 /* 5 */
                                          :
                                          16 /* 5 */
                                    )
                                    :
                                    (x < 1 << 18 ?
                                          17 /* 5 */
                                          :
                                          18 /* 5 */
                                    )
                              )
                              :
                              (x < 1 << 21 ?
                                    (x < 1 << 20 ?
                                          19 /* 5 */
                                          :
                                          20 /* 5 */
                                    )
                                    :
                                    (x < 1 << 22 ?
                                          21 /* 5 */
                                          :
                                          22 /* 5 */
                                    )
                              )
                        )
                        :
                        (x < 1 << 27 ?
                              (x < 1 << 25 ?
                                    (x < 1 << 24 ?
                                          23 /* 5 */
                                          :
                                          24 /* 5 */
                                    )
                                    :
                                    (x < 1 << 26 ?
                                          25 /* 5 */
                                          :
                                          26 /* 5 */
                                    )
                              )
                              :
                              (x < 1 << 29 ?
                                    (x < 1 << 28 ?
                                          27 /* 5 */
                                          :
                                          28 /* 5 */
                                    )
                                    :
                                    (x < 1 << 30 ?
                                          29 /* 5 */
                                          :
                                          30 /* 5 */
                                    )
                              )
                        )
                  )
            );
   }

   private IOUtil() {
      // Only static access
   }
}
