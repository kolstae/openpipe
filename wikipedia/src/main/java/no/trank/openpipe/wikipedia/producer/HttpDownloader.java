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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.util.HexUtil;
import no.trank.openpipe.wikipedia.producer.meta.RssMetaParser;

/**
 * @version $Revision$
 */
public class HttpDownloader {
   private static final Logger log = LoggerFactory.getLogger(HttpDownloader.class);
   private File targetFile;
   private String rssUrl;
   private long progressTimeStamp;
   private List<DownloadProgressListener> progressListeners = new ArrayList<DownloadProgressListener>(1);
   private int progressInterval = 10000;
   private URL sourceUrl;
   private HttpClient httpClient;

   public File getTargetFile() {
      return targetFile;
   }

   public void setTargetFile(File targetFile) {
      this.targetFile = targetFile;
   }

   public String getRssUrl() {
      return rssUrl;
   }

   public void setRssUrl(String rssUrl) {
      this.rssUrl = rssUrl;
   }

   public int getProgressInterval() {
      return progressInterval;
   }

   public void setProgressInterval(int progressInterval) {
      this.progressInterval = progressInterval;
   }

   public URL getSourceUrl() {
      return sourceUrl;
   }

   public void setHttpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
   }

   public void addProgressListener(DownloadProgressListener progressListener) {
      progressListeners.add(progressListener);
   }

   public void init() {
      if (targetFile == null || !targetFile.getParentFile().isDirectory()) {
         throw new IllegalArgumentException("Invalid targetFile '" + targetFile + '\'');
      }
      if (rssUrl == null) {
         throw new NullPointerException("sourceUrl cannot be null");
      }
      try {
         new URI(rssUrl, true);
      } catch (URIException e) {
         throw new IllegalArgumentException("rssUrl '" +rssUrl+ "' must be a valid URL: " + e.getMessage());
      }
      if (httpClient == null) {
         httpClient = new HttpClient();
      }
   }

   public boolean isLastVersion() throws IOException {
      final RssMetaParser parser = new RssMetaParser(getContentAsString(rssUrl));
      sourceUrl = parser.getFileUrl();
      return !isModified(parser.getModifiedDate(), targetFile.lastModified()) || hasCorrectMd5(parser);
   }

   private boolean hasCorrectMd5(RssMetaParser parser) throws IOException {
      final String md5Sums = getContentAsString(parser.getMd5Url().toExternalForm());
      return isSameMd5(targetFile, parser.findMd5(md5Sums));
   }

   private String getContentAsString(String url) throws IOException {
      final GetMethod get = new GetMethod(url);
      try {
         final int status = httpClient.executeMethod(get);
         if (status >= 200 && status < 300) {
            return get.getResponseBodyAsString();
         }
         log.warn("Could not get content from url: '{}' status: {}", url, status);
         return null;
      } finally {
         get.releaseConnection();
      }
   }

   private static boolean isModified(Date modifiedDate, long localDate) {
      return modifiedDate.after(new Date(localDate));
   }

   private static boolean isSameMd5(File file, String md5Sum) {
      File md5File = new File(file.getAbsolutePath() + ".md5");
      try {
         if (md5File.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(md5File));
            try {
               String localMd5 = reader.readLine().trim();
               return md5Sum.equalsIgnoreCase(localMd5);
            } finally {
               reader.close();
            }
         }
      } catch (FileNotFoundException e) {
         // Do nothing
      } catch (IOException e) {
         log.error("Could not read md5 from file: " + md5File.getAbsolutePath());
      }
      return false;
   }


   public int downloadFile() throws IOException {
      final GetMethod get = new GetMethod(sourceUrl.toExternalForm());
      try {
         final int status = httpClient.executeMethod(get);
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
      } finally {
         get.releaseConnection();
      }
   }

   protected void writeFile(InputStream in, long size) throws IOException {
      OutputStream out;
      try {
         out = new DigestOutputStream(new FileOutputStream(targetFile), MessageDigest.getInstance("MD5"));
      } catch (NoSuchAlgorithmException e) {
         log.error("Could not make md5. MD5 not supported", e);
         out = new FileOutputStream(targetFile);
      }
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
         if (out instanceof DigestOutputStream) {
            writeMd5File(((DigestOutputStream)out).getMessageDigest());
         }
      }
   }

   private void writeMd5File(MessageDigest messageDigest) throws IOException {
      File md5File = new File(targetFile.getAbsolutePath() + ".md5");
      FileOutputStream fout = new FileOutputStream(md5File);
      try {         
         fout.write(HexUtil.toHexString(messageDigest.digest()).getBytes("UTF-8"));
      } finally {
         try {
            fout.close();
         } catch (Exception e) {
            // Do nothing
         }
      }
   }

   private void progress(long size, long totalReadBytes) {
      final long currentTime = System.currentTimeMillis();
      if (progressTimeStamp + progressInterval < currentTime || size == totalReadBytes) {
         for (DownloadProgressListener progressListener : progressListeners) {
            progressListener.progress(size, totalReadBytes);
         }
         progressTimeStamp = currentTime;
      }
   }
}
