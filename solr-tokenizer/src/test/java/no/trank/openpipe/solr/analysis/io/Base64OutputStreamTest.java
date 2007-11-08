/*
 * Copyright 2007  T-Rank AS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.trank.openpipe.solr.analysis.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class Base64OutputStreamTest extends TestCase {
   public static final String TEXT1 = "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";
   public static final String RES1 = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
   public static final String TEXT2 = TEXT1.substring(0, TEXT1.length() - 1);
   public static final String RES2 = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZQ==";
   public static final String TEXT3 = TEXT1.substring(0, TEXT1.length() - 2);
   public static final String RES3 = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3Vy";
   public static final String TEXT4 = TEXT1.substring(0, TEXT1.length() - 3);
   public static final String RES4 = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3U=";
   private static final Charset CHARSET = Charset.forName("UTF-8");

   public void testWrite() throws Exception {
      final ByteArrayOutputStream bout = new ByteArrayOutputStream();
      test(TEXT1, RES1, true, bout);
      test(TEXT2, RES2, false, bout);
      test(TEXT3, RES3, false, bout);
      test(TEXT4, RES4, true, bout);
   }

   public void testWriteLong() throws Exception {
      final File file = File.createTempFile("base64", ".tmp");
      file.deleteOnExit();
      final Base64OutputStream out = new Base64OutputStream(new FileOutputStream(file));
      final Random rnd = new Random();
      final byte[] bytes = new byte[1025 + rnd.nextInt(3072)];
      final int itCount = 1024 * 1024 * 2 / bytes.length;
      try {
         for (int i = 0; i < itCount; i++) {
            rnd.nextBytes(bytes);
            out.write(bytes);
         }
      } finally {
         out.close();
      }
      final Base64InputStream in = new Base64InputStream(new FileReader(file));
      try {
         int count = 0;
         int len;
         while ((len = in.read(bytes)) > 0) {
            count += len;
         }
         assertEquals(bytes.length * itCount, count);
      } finally {
         in.close();
      }
   }

   private static void test(String text, String result, boolean writeAllBytes,  ByteArrayOutputStream bout) 
         throws IOException {
      final Base64OutputStream out = new Base64OutputStream(bout);
      if (writeAllBytes) {
         final byte[] b = text.getBytes(CHARSET);
         out.write(b, 0, 7);
         out.write(b, 7, b.length - 7);
      } else {
         for (int i = 0; i < text.length(); i++) {
            out.write(text.charAt(i));
         }
      }
      out.close();
      assertEquals(result, bout.toString());
      bout.reset();
   }
}
