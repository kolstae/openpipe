package no.trank.openpipe.opennlp.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import opennlp.maxent.io.BinaryGISModelReader;

/**
 * @version $Revision: 874 $
 */
public class InputStreamGISModelReader extends BinaryGISModelReader {

   public InputStreamGISModelReader(InputStream in) throws IOException {
      this(in, false);
   }
   
   public InputStreamGISModelReader(InputStream in, boolean gzipped) throws IOException {
      super(gzipped ? new DataInputStream(new GZIPInputStream(in)) : new DataInputStream(in));
   }
}
