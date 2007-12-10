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
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.util.HexUtil;

/**
 * @version $Revision$
 */
public class HttpDownloader {
   private static final Logger log = LoggerFactory.getLogger(HttpDownloader.class);
   private File targetFile;
   private String sourceUrl;
   private long progressTimeStamp;
   private List<DownloadProgressListener> progressListeners = new ArrayList<DownloadProgressListener>();
   private int progressInterval = 1000;

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

   public int getProgressInterval() {
      return progressInterval;
   }

   public void setProgressInterval(int progressInterval) {
      this.progressInterval = progressInterval;
   }

   public void addProgressListener(DownloadProgressListener progressListener) {
      progressListeners.add(progressListener);
   }

   public boolean isLastVersion() throws IOException {
      int idx = sourceUrl.lastIndexOf('/');
      String baseUrl = sourceUrl.substring(0, idx);
      String wikiArtifactName = sourceUrl.substring(idx + 1, sourceUrl.indexOf('-', idx + 1));
      String md5SumsUrl = baseUrl + '/' + wikiArtifactName +"-latest-md5sums.txt";
      log.debug("Dowloading md5Sums");
      HttpClient httpClient = new HttpClient();
      GetMethod get = new GetMethod(md5SumsUrl);
      int status = httpClient.executeMethod(get);
      if (status >= 200 && status < 300) {
         final String md5sums = get.getResponseBodyAsString();
         final Pattern p = Pattern.compile("\\n(\\S+)\\s+(" + wikiArtifactName + "-(\\d+)-pages-articles\\.xml\\.bz2)");
         final Matcher matcher = p.matcher(md5sums);
         if (matcher.find()) {
            String md5Sum = matcher.group(1);
            return isSameMd5(targetFile, md5Sum);
         }
      } else {
         log.debug("Could not download md5 sums.");
      }
      return false;
   }

   private boolean isSameMd5(File file, String md5Sum) {
      File md5File = new File(file.getAbsolutePath() + ".md5");
      try {
         if (md5File.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(md5File));
            String localMd5 = reader.readLine().trim();
            return md5Sum.equalsIgnoreCase(localMd5);
         }
      } catch (FileNotFoundException e) {
         // Do nothing
      } catch (IOException e) {
         log.error("Could not read md5 from file: " + md5File.getAbsolutePath());
      }
      return false;
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
