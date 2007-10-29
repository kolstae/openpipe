package no.trank.openpipe.solr.analysis;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import no.trank.openpipe.solr.util.IOUtil;

/**
 * Static utility for writing and reading/verifying header info. Used for ensure version compatibility between 
 * de-/serializing.
 * 
 * @version $Revision$
 */
public class BinaryIO {
   private static final int VERSION = 1;
   private static final int COMPRESSED = 0x40;
   private static final int VERSION_MASK = 0x3f;

   /**
    * Writes a header with the current version and compressed flag to the given stream.
    * The header is written using {@link IOUtil#writeNibble(OutputStream, int)}.
    * 
    * @param out the stream to write to.
    * @param compress whether to indicate compression.
    * 
    * @throws IOException if an I/O error occures.
    * 
    * @see #readHeader(InputStream)
    */
   public static void writeHeader(OutputStream out, boolean compress) throws IOException {
      IOUtil.writeNibble(out, compress ? VERSION | COMPRESSED : VERSION);
   }

   /**
    * Reads and validates the header from the input stream and finds whether the stream is compressed.
    * 
    * @param in the input stream to read from.
    * 
    * @return <tt>true</tt> if the header indicates that the stream is compressed.
    * 
    * @throws IOException if an I/O error occures or the incompatible/invalid version was detected.
    * @throws EOFException if end-of-stream was reached.
    * 
    * @see #readHeader(InputStream)
    * @see #isCompressed(int)
    */
   public static boolean readHeaderIsCompressed(InputStream in) throws IOException {
      return isCompressed(readHeader(in));
   }

   /**
    * Reads and validates the header from the input stream. 
    * The header is read using {@link IOUtil#readNibble(InputStream)}.
    * 
    * @param in the input stream to read from.
    * 
    * @return the header as an int.
    * 
    * @throws IOException if an I/O error occures or the incompatible/invalid version was detected.
    * @throws EOFException if end-of-stream was reached.
    * 
    * @see #writeHeader(OutputStream, boolean)
    */
   public static int readHeader(InputStream in) throws IOException {
      final int header = IOUtil.readNibble(in);
      if (header == -1) {
         throw new EOFException();
      }
      if ((header & VERSION_MASK) != VERSION) {
         throw new IOException("Unknown version " + (header & VERSION_MASK));
      }
      if (header > (COMPRESSED | VERSION)) {
         throw new IOException("Invalid version " + header);
      }
      return header;
   }

   /**
    * Checks whether this header indicates compressed data.
    * 
    * @param header the header to check.
    * 
    * @return <tt>true</tt> if the header indicates that the stream is compressed.
    */
   public static boolean isCompressed(int header) {
      return (header & COMPRESSED) == COMPRESSED;
   }

   private BinaryIO() {
      // Only static access
   }
}
