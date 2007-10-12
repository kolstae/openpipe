package no.trank.openpipe.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;

/**
 * @version $Revision$
 */
public class TextFileDocumentReader extends FileDocumentReader {
   private static final Logger log = LoggerFactory.getLogger(TextFileDocumentReader.class);
   private String bodyField = "body";
   private String encoding;

   @Override
   protected FileDocReader createReader() {
      if (bodyField == null) {
         throw new NullPointerException("bodyField cannot be null");
      }
      final Charset charset;
      if (encoding == null) {
         charset = Charset.defaultCharset();
         log.info("Using default encoding '{}'", charset.name());
      } else {
         charset = Charset.forName(encoding);
      }
      return new TextFileDocReader(bodyField, charset);
   }

   public String getBodyField() {
      return bodyField;
   }

   public void setBodyField(String bodyField) {
      this.bodyField = bodyField;
   }

   public String getEncoding() {
      return encoding;
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   private static class TextFileDocReader implements FileDocReader {
      private final String bodyField;
      private final Charset charset;

      public TextFileDocReader(String bodyField, Charset charset) {
         this.bodyField = bodyField;
         this.charset = charset;
      }

      public Document getDocument(File file) {
         try {
            final Reader reader = new InputStreamReader(new FileInputStream(file), charset);
            try {
               final StringBuilder sb = new StringBuilder((int) file.length());
               final char[] cbuf = new char[2048];
               int bytesread = reader.read(cbuf);
               while (bytesread > 0) {
                  sb.append(cbuf, 0, bytesread);
                  bytesread = reader.read(cbuf);
               }
               final Document doc = new Document();
               doc.setFieldValue(bodyField, sb.toString());
               doc.setOperation(DocumentOperation.ADD_VALUE);
               return doc;
            } finally {
               try {
                  reader.close();
               } catch (IOException e) {
                  // Ignoring
               }
            }
         } catch (FileNotFoundException e) {
            throw new RuntimeException("Coule not read file", e);
         } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Coule not read file", e);
         } catch (IOException e) {
            throw new RuntimeException("Coule not read file", e);
         }
      }
   }
}
