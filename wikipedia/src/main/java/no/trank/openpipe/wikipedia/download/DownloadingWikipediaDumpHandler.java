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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.wikipedia.WikipediaDumpHandler;

/**
 * @version $Revision$
 */
public class DownloadingWikipediaDumpHandler implements WikipediaDumpHandler {
   private static final Logger log = LoggerFactory.getLogger(DownloadingWikipediaDumpHandler.class);
   private HttpDownloader httpDownloader;
   private boolean isNew;
   private boolean initialized;

   public void init() {
      if (!initialized) {
         initialized = true;
         httpDownloader.init();
         isNew = downloadWiki();
      }
   }

   @Override
   public boolean isNewDump() {
      init();
      return isNew;
   }

   @Override
   public File getDumpFile() {
      init();
      return httpDownloader.getTargetFile();
   }

   private boolean downloadWiki() {
      try {
         if (!httpDownloader.isLastVersion()) {
            try {
               log.info("Downloading {}", httpDownloader.getSourceUrl());
               final int status = httpDownloader.downloadFile();
               if (status < 200 || status >= 300) {
                  throw new RuntimeException("Could not download dump:" + httpDownloader.getSourceUrl() +
                        " http status: " + status);
               }
               return true;
            } catch (IOException e) {
               throw new RuntimeException("Could not download dump: " + httpDownloader.getSourceUrl(), e);
            }
         } else {
            log.info("Found local file with correct md5. Skipping download.");
         }
      } catch (IOException e) {
         log.error("Could not determine last version.", e);
      }
      return false;
   }

   public void setHttpDownloader(HttpDownloader httpDownloader) {
      this.httpDownloader = httpDownloader;
   }
}
