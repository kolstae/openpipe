package no.trank.openpipe.solr.analysis;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.apache.lucene.analysis.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.solr.analysis.io.Base64OutputBuffer;
import static no.trank.openpipe.solr.util.IOUtil.writeNibble;
import static no.trank.openpipe.solr.util.IOUtil.writeUTF;

/**
 * @version $Revision$
 */
public class Base64TokenSerializer implements TokenSerializer {
   private static final Logger log = LoggerFactory.getLogger(Base64TokenSerializer.class);
   private Base64OutputBuffer out;
   private int maxBufferSize = 128 * 1024;
   private int compressionThreshold = 1024;
   private Deflater deflater;

   public String serialize(AnnotatedField field) {
      final AnnotationTokenStream stream = new AnnotationTokenStream(field);
      if (out == null) {
         out = new Base64OutputBuffer();
      } else {
         out.reset();
      }
      try {
         try {
            final String value = field.getValue();
            final boolean compress = value.length() > compressionThreshold;
            Base64IO.writeHeader(out, compress);
            final OutputStream out = getOutputStream(compress);
            writeUTF(out, value);
            for (Token tok = stream.next(); tok != null; tok = stream.next()) {
               final int start = tok.startOffset();
               writeNibble(out, start);
               writeNibble(out, tok.endOffset() - start);
               writeNibble(out, tok.getPositionIncrement());
               writeUTF(out, tok.termText());
               writeUTF(out, tok.type());
            }
            out.close();
            final String res = this.out.toString();
            log.debug(res);
            return res;
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } finally {
         out.trimToSize(maxBufferSize);
      }
   }

   private OutputStream getOutputStream(boolean compress) {
      if (compress) {
         if (deflater == null) {
            deflater = new Deflater(Deflater.BEST_COMPRESSION);
         } else {
            deflater.reset();
         }
         return new DeflaterOutputStream(out, deflater);
      }
      return out;
   }

   public int getMaxBufferSize() {
      return maxBufferSize;
   }

   public void setMaxBufferSize(int maxBufferSize) {
      this.maxBufferSize = maxBufferSize;
   }

   public int getCompressionThreshold() {
      return compressionThreshold;
   }

   public void setCompressionThreshold(int compressionThreshold) {
      this.compressionThreshold = compressionThreshold;
   }

   public void close() {
      if (deflater != null) {
         deflater.end();
         deflater = null;
      }
      out = null;
   }
}