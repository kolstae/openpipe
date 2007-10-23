package no.trank.openpipe.solr.analysis;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.lucene.analysis.Token;

import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.solr.analysis.io.Base64OutputStream;
import static no.trank.openpipe.solr.util.IOUtil.writeNibble;
import static no.trank.openpipe.solr.util.IOUtil.writeUTF;

/**
 * @version $Revision$
 */
public class Base64TokenSerializer implements TokenSerializer {
   private Base64OutputStream bout;
   private DataOutputStream out;
   private int maxBufferSize = 128 * 1024;

   public String serialize(AnnotatedField field) {
      final AnnotationTokenStream stream = new AnnotationTokenStream(field);
      if (bout == null) {
         bout = new Base64OutputStream();
         out = new DataOutputStream(bout);
      } else {
         bout.reset();
      }
      try {
         Token tok;
         try {
            while ((tok = stream.next()) != null) {
               final int start = tok.startOffset();
               writeNibble(out, start);
               writeNibble(out, tok.endOffset() - start);
               writeNibble(out, tok.getPositionIncrement());
               writeUTF(out, tok.termText());
               writeUTF(out, tok.type());
            }
            out.flush();
            return bout.toString();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } finally {
         bout.trimToSize(maxBufferSize);
      }
   }

   public int getMaxBufferSize() {
      return maxBufferSize;
   }

   public void setMaxBufferSize(int maxBufferSize) {
      this.maxBufferSize = maxBufferSize;
   }
}