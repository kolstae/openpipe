package no.trank.openpipe.parse.text;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.parse.api.ParseData;

/**
 * @version $Revision$
 */
public class TextDecoder implements Closeable {
   private static final Logger log = LoggerFactory.getLogger(TextParser.class);
   private static final int CAPACITY = 4096 * 16;
   private final Set<CharsetDecoder> decoders = new LinkedHashSet<CharsetDecoder>();
   private final Map<Charset, CharsetDecoder> decoderMap = new HashMap<Charset, CharsetDecoder>();
   private final CharsetDetector detector;
   private String encoding;
   private CharBuffer charBuffer;
   private ByteBuffer directByteBuffer;
   private ByteBuffer byteBuffer;

   public TextDecoder() {
      this("UTF-8");
   }

   public TextDecoder(String encoding) {
      this(Arrays.asList(encoding));
   }

   public TextDecoder(List<String> encodings) {
      this(encodings, null);
   }

   public TextDecoder(CharsetDetector detector) {
      this.detector = detector;
   }

   public TextDecoder(List<String> encodings, CharsetDetector detector) {
      this.detector = detector;
      for (String enc : encodings) {
         decoders.add(getDecoder(Charset.forName(enc)));
      }
      if (decoders.isEmpty() && detector == null) {
         throw new IllegalArgumentException("No encodings/detector");
      }
   }

   private CharsetDecoder getDecoder(Charset charset) throws UnsupportedCharsetException {
      final CharsetDecoder dec = decoderMap.get(charset);
      if (dec != null) {
         return dec;
      }
      final CharsetDecoder decoder = charset.newDecoder();
      decoder.onMalformedInput(CodingErrorAction.REPORT);
      decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
      decoderMap.put(charset, decoder);
      return decoder;
   }

   public String decode(ParseData data) throws IOException {
      encoding = null;
      final InputStream in = data.getInputStream();
      final FileChannel channel = getChannel(in);
      final StringBuilder buf = new StringBuilder(data.getLength());
      if (channel != null) {
         return decode(channel, getDirectByteBuffer(), getCharBuffer(), buf);
      }
      return decode(in, data, getByteBuffer(), getCharBuffer(), buf);
   }

   private String decode(InputStream in, ParseData data, ByteBuffer bBuf, CharBuffer cBuf, StringBuilder buf) 
         throws IOException {
      if (detector != null) {
         final int len = read(in, bBuf, bBuf.array());
         final byte[] bytes = bBuf.capacity() != len ? Arrays.copyOf(bBuf.array(), len) : bBuf.array();
         final String enc = detect(bytes);
         close(in);
         if (enc != null) {
            final CharsetDecoder decoder = getDecoder(Charset.forName(enc));
            final String text = decode(data.getInputStream(), bBuf, cBuf, buf, decoder);
            if (text != null) {
               foundEncoding(decoder.charset());
               return text;
            }
         }
         in = data.getInputStream();
      }
      for (CharsetDecoder decoder : decoders) {
         String text = decode(in, bBuf, cBuf, buf, decoder);
         if (text != null) {
            foundEncoding(decoder.charset());
            return text;
         } else if (decoder.isAutoDetecting() && decoder.isCharsetDetected()) {
            final Charset charset = decoder.detectedCharset();
            text = decode(data.getInputStream(), bBuf, cBuf, buf, getDecoder(charset));
            if (text != null) {
               foundEncoding(charset);
               return text;
            }
         }
         in = data.getInputStream();
      }
      close(in);
      encoding = null;
      return null;
   }

   private void foundEncoding(Charset charset) {
      encoding = charset.name();
      log.debug("Decoded stream with detected charset {}", encoding);
   }

   private static String decode(InputStream in, ByteBuffer bBuf, CharBuffer cBuf, StringBuilder buf, 
         CharsetDecoder decoder) throws IOException {
      try {
         reset(bBuf, cBuf, buf, decoder);
         final byte[] bytes = bBuf.array();
         while (read(in, bBuf, bytes) >= 0) {
            if (decodeBuffer(bBuf, cBuf, buf, decoder)) return null;
         }
         return flushDecoder(bBuf, cBuf, buf, decoder);
      } finally {
         close(in);
      }
   }

   private static void close(InputStream in) {
      try {
         in.close();
      } catch (IOException e) {
         // Ignoring
      }
   }

   private static int read(InputStream in, ByteBuffer bBuf, byte[] bytes) throws IOException {
      final int pos = bBuf.position();
      final int read = in.read(bytes, pos, bBuf.remaining());
      if (read > 0) {
         bBuf.position(pos + read);
      }
      return read;
   }

   private String decode(FileChannel channel, ByteBuffer bBuf, CharBuffer cBuf, StringBuilder buf) throws IOException {
      try {
         if (detector != null) {
            final int len = channel.read(bBuf);
            bBuf.flip();
            final byte[] bytes = new byte[len];
            bBuf.get(bytes);
            final String enc = detect(bytes);
            if (enc != null) {
               final CharsetDecoder decoder = getDecoder(Charset.forName(enc));
               final String text = decode(channel, bBuf, cBuf, buf, decoder);
               if (text != null) {
                  foundEncoding(decoder.charset());
                  return text;
               }
            }
         }
         for (CharsetDecoder decoder : decoders) {
            String text = decode(channel, bBuf, cBuf, buf, decoder);
            if (text != null) {
               foundEncoding(decoder.charset());
               return text;
            } else if (decoder.isAutoDetecting() && decoder.isCharsetDetected()) {
               final Charset charset = decoder.detectedCharset();
               text = decode(channel, bBuf, cBuf, buf, getDecoder(charset));
               if (text != null) {
                  foundEncoding(charset);
                  return text;
               }
            }
         }
      } finally {
         try {
            channel.close();
         } catch (IOException e) {
            // Ignoring
         }
      }
      return null;
   }

   private String detect(byte[] bytes) {
      detector.setText(bytes);
      final CharsetMatch match = detector.detect();
      if (log.isDebugEnabled()) {
         log.debug("Detector has confidence: {} encoding {} lang {}", 
               new Object[] {match.getConfidence(), match.getName(), match.getLanguage()});
      }
      return match.getConfidence() > 50 ? match.getName() : null;
   }

   private static String decode(FileChannel channel, ByteBuffer bBuf, CharBuffer cBuf, StringBuilder buf, 
         CharsetDecoder decoder) throws IOException {
      channel.position(0);
      reset(bBuf, cBuf, buf, decoder);
      while (channel.read(bBuf) >= 0) {
         if (decodeBuffer(bBuf, cBuf, buf, decoder)) return null;
      }
      return flushDecoder(bBuf, cBuf, buf, decoder);
   }

   private static void reset(ByteBuffer bBuf, CharBuffer cBuf, StringBuilder buf, CharsetDecoder decoder) {
      decoder.reset();
      bBuf.clear();
      cBuf.clear();
      buf.setLength(0);
   }

   private static String flushDecoder(ByteBuffer bBuf, CharBuffer cBuf, StringBuilder buf, CharsetDecoder decoder) {
      if (bBuf.position() == 0) {
         bBuf.limit(0);
      } else {
         bBuf.flip();
      }
      if (decoder.decode(bBuf, cBuf, true).isError()) {
         return null;
      }
      decoder.flush(cBuf);
      if (cBuf.position() > 0) {
         cBuf.flip();
         buf.append(cBuf.array(), cBuf.arrayOffset(), cBuf.remaining());
      }
      return buf.toString();
   }

   private static boolean decodeBuffer(ByteBuffer bBuf, CharBuffer cBuf, StringBuilder buf, CharsetDecoder decoder) {
      bBuf.flip();
      if (decoder.decode(bBuf, cBuf, false).isError()) {
         return true;
      }
      cBuf.flip();
      buf.append(cBuf.array(), cBuf.arrayOffset(), cBuf.remaining());
      bBuf.compact();
      cBuf.clear();
      return false;
   }

   private static FileChannel getChannel(InputStream in) {
      if (FileInputStream.class.equals(in.getClass())) {
         return ((FileInputStream)in).getChannel();
      }
      return null;
   }

   private ByteBuffer getDirectByteBuffer() {
      if (directByteBuffer == null) {
         directByteBuffer = ByteBuffer.allocateDirect(CAPACITY);
      } else {
         directByteBuffer.clear();
      }
      return directByteBuffer;
   }

   private ByteBuffer getByteBuffer() {
      if (byteBuffer == null) {
         byteBuffer = ByteBuffer.allocate(CAPACITY);
      } else {
         byteBuffer.clear();
      }
      return byteBuffer;
   }

   private CharBuffer getCharBuffer() {
      if (charBuffer == null) {
         charBuffer = CharBuffer.allocate(CAPACITY);
      } else {
         charBuffer.clear();
      }
      return charBuffer;
   }

   public String getEncoding() {
      return encoding;
   }

   public void close() throws IOException {
      decoderMap.clear();
      byteBuffer = null;
      directByteBuffer = null;
      charBuffer = null;
   }
}