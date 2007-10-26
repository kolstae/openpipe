package no.trank.openpipe.solr.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import no.trank.openpipe.solr.util.IOUtil;

/**
 * @version $Revision$
 */
public class Base64IO {
   private static final int VERSION = 1;
   private static final int COMPRESSED = 0x40;
   private static final int VERSION_MASK = 0x3f;

   public static void writeHeader(OutputStream out, boolean compress) throws IOException {
      IOUtil.writeNibble(out, compress ? VERSION | COMPRESSED : VERSION);
   }

   public static boolean readHeaderIsCompressed(InputStream in) throws IOException {
      return isCompressed(readHeader(in));
   }
   
   public static int readHeader(InputStream in) throws IOException {
      final int version = IOUtil.readNibble(in);
      if ((version & VERSION_MASK) != VERSION) {
         throw new IOException("Unknown version " + (version & VERSION_MASK));
      } else if (version > (COMPRESSED | VERSION)) {
         throw new IOException("Invalid version " + version);
      }
      return version;
   }

   public static boolean isCompressed(int version) {
      return (version & COMPRESSED) == COMPRESSED;
   }
}
