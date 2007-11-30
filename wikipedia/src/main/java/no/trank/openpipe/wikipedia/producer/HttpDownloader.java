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
package no.trank.openpipe.wikipedia.producer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision$
 */
public class HttpDownloader {
   private static final Logger log = LoggerFactory.getLogger(HttpDownloader.class);
   private File targetFile;
   private String sourceUrl;
   private long progressTimeStamp;
   private List<DownloadProgressListener> progressListeners = new ArrayList<DownloadProgressListener>();

   public File getTargetFile() {
      return targetFile;
   }

   public void setTargetFile(File targetFile) {
      this.targetFile = targetFile;
   }

   public String getSourceUrl() {
      return sourceUrl;
   }

   public void setSourceUrl(String sourceUrl) {
      this.sourceUrl = sourceUrl;
   }

   public void addProgressListener(DownloadProgressListener progressListener) {
      progressListeners.add(progressListener);
   }

   public int downloadFile() throws IOException {
      HttpClient httpClient = new HttpClient();
      GetMethod get = new GetMethod(sourceUrl);
      int status = httpClient.executeMethod(get);
      if (status >= 200 && status < 300) {
         long size = get.getResponseContentLength();
         log.debug("Downloading: " + size + " bytes");
         final BufferedInputStream in = new BufferedInputStream(get.getResponseBodyAsStream());
         try {
            writeFile(in, size);
         } finally {
            in.close();
         }
      }
      return status;
   }

   protected void writeFile(InputStream in, long size) throws IOException {
      final FileOutputStream out = new FileOutputStream(targetFile);
      try {
         long totalReadBytes = 0;
         byte[] buf = new byte[16384];
         int numBytes = in.read(buf);
         while(numBytes >= 0) {
            totalReadBytes += numBytes;
            progress(size, totalReadBytes);
            out.write(buf, 0, numBytes);
            numBytes = in.read(buf);
         }
      } finally {
         try {
            out.close();
         } catch (Exception e) {
            // Do nothing
         }
         progress(size, size);
      }
   }

   private void progress(long size, long totalReadBytes) {
      final long currentTime = System.currentTimeMillis();
      if (progressTimeStamp + 1000 < currentTime || size == totalReadBytes) {
         for (DownloadProgressListener progressListener : progressListeners) {
            progressListener.progress(size, totalReadBytes);
         }
         progressTimeStamp = currentTime;
      }
   }
}
