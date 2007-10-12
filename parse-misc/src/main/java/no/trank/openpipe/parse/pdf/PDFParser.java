package no.trank.openpipe.parse.pdf;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.Arrays;

import org.pdfbox.io.RandomAccess;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

/**
 * @version $Revision: 874 $
 */
public class PDFParser implements Parser, Closeable {
   private final PDFTextStripper stripper;
   private final SBWriter writer;
   private final RandomAccessImpl scratchFile;

   public PDFParser() {
      try {
         stripper = new PDFTextStripper();
         scratchFile = new RandomAccessImpl(1024 * 1024);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      stripper.setSortByPosition(true);
      writer = new SBWriter();
   }

   public ParserResult parse(ParseData data) throws IOException, ParserException {
      final PDDocument doc = PDDocument.load(data.getInputStream(), scratchFile);
      try {
         writer.reset();
         try {
            stripper.writeText(doc, writer);
            final ParserResultImpl result = new ParserResultImpl();
            result.setText(writer.toString());
            result.setTitle(doc.getDocumentInformation().getTitle());
            return result;
         } finally {
            writer.trimToMaxSize(1024 * 64);
         }
      } finally {
         try {
            doc.close();
         } catch (IOException e) {
            // Ignoring
         }
      }
   }

   public void close() throws IOException {
      scratchFile.release();
   }

   private static class SBWriter extends Writer {
      private final StringBuilder buf = new StringBuilder(4096);

      @Override
      public void write(char cbuf[], int off, int len) {
         buf.append(cbuf, off, len);
      }

      @Override
      public void write(int c) {
         buf.append(c);
      }

      @Override
      public void write(char cbuf[]) {
         buf.append(cbuf);
      }

      @Override
      public void write(String str) {
         buf.append(str);
      }

      @Override
      public void write(String str, int off, int len) {
         buf.append(str, off, off + len);
      }

      @Override
      public Writer append(CharSequence csq) {
         buf.append(csq);
         return this;
      }

      @Override
      public Writer append(CharSequence csq, int start, int end) {
         buf.append(csq, start, end);
         return this;
      }

      @Override
      public Writer append(char c) {
         buf.append(c);
         return this;
      }

      @Override
      public void flush() {
      }

      @Override
      public void close() {
      }

      @Override
      public String toString() {
         return buf.toString();
      }

      public void trimToMaxSize(int maxSize) {
         if (buf.capacity() > maxSize) {
            buf.setLength(maxSize);
            buf.trimToSize();
         }
      }

      public int capacity() {
         return buf.capacity();
      }

      public void reset() {
         buf.setLength(0);
      }
   }

   private static class RandomAccessImpl implements RandomAccess {
      private final byte[] buf;
      private final RandomAccessFile ra;
      private int pointer;
      private int size;

      private RandomAccessImpl(int bufferSize) throws IOException {
         buf = new byte[bufferSize];
         final File file = File.createTempFile("pdfParser", ".dat");
         file.deleteOnExit();
         ra = new RandomAccessFile(file, "rw");
      }

      public void close() throws IOException {
         pointer = 0;
         size = 0;
         Arrays.fill(buf, (byte) 0);
         ra.setLength(0);
         ra.seek(0);
      }

      public void seek(long position) throws IOException {
         final long raSeek = position - buf.length;
         if (raSeek > 0) {
            ra.seek(raSeek);
            pointer = buf.length;
         } else {
            ra.seek(0);
            pointer = (int) position;
         }
      }

      public int read() throws IOException {
         if (pointer >= buf.length) {
            return ra.read();
         } else if (pointer > size) {
            return -1;
         }
         return (int) buf[pointer++] & 0xff;
      }

      public int read(byte[] b, int offset, int length) throws IOException {
         final int len = Math.min(length, size - pointer);
         if (len > 0) {
            System.arraycopy(buf, pointer, b, offset, len);
            pointer += len;
         }
         if (size >= buf.length) {
            final int remaining = length - len;
            if (remaining > 0) {
               return len + ra.read(b, offset + len, remaining);
            }
         }
         return len;
      }

      public long length() throws IOException {
         if (size >= buf.length) {
            return buf.length + ra.length();
         }
         return size;
      }

      public void write(int b) throws IOException {
         if (pointer >= buf.length) {
            size = buf.length;
            ra.write(b);
         } else {
            buf[pointer++] = (byte) b;
            if (pointer > size) {
               size = pointer;
            }
         }
      }

      public void write(byte[] b, int offset, int length) throws IOException {
         final int len = Math.min(length, buf.length - pointer);
         if (len > 0) {
            System.arraycopy(b, offset, buf, pointer, len);
            pointer += len;
            if (pointer > size) {
               size = pointer;
            }
         }
         final int remaining = length - len;
         if (remaining > 0) {
            ra.write(b, offset + len, remaining);
            size = buf.length;
         }
      }
      
      public void release() throws IOException {
         ra.close();
      }
   }
}
