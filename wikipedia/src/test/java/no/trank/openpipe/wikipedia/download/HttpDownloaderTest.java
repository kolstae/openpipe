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
package no.trank.openpipe.wikipedia.download;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

public class HttpDownloaderTest extends TestCase {
   HttpDownloader httpDownloader;

   public void testWriteFile() throws Exception {
      httpDownloader = new HttpDownloader();
      File testOutput = File.createTempFile("httpDownloaderTest", ".tmp");
      testOutput.deleteOnExit();
      try {
         httpDownloader.setTargetFile(testOutput);
         ByteArrayInputStream in = new ByteArrayInputStream(new byte[]{'a','b','c'});
         httpDownloader.writeFile(in, 0);

         byte[] testbuf = new byte[10];
         FileInputStream testin = new FileInputStream(testOutput);
         try {
            assertEquals(3, testin.read(testbuf));
            assertEquals('a', testbuf[0]);
            assertEquals('b', testbuf[1]);
            assertEquals('c', testbuf[2]);            
         } finally {
            testin.close();
         }
      } finally {
         testOutput.delete();
      }
   }
}