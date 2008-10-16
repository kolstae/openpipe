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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.util.log.DefaultTimedLogger;
import no.trank.openpipe.util.log.TotalTimedLogger;

/**
 * @version $Revision$
*/
public class DownloadProgressLogger implements DownloadProgressListener {
   protected final TotalTimedLogger logger;
   protected long kb;

   public DownloadProgressLogger() {
      this(LoggerFactory.getLogger(DownloadProgressLogger.class));
   }

   public DownloadProgressLogger(Logger log) {
      logger = new TotalTimedLogger(log,
            "%1$d/%5$d [%6$4.1f%%] (%3$d) kb at %2$.2f (%4$.2f) kb/s", TimeUnit.SECONDS,
            DefaultTimedLogger.Calculator.UNIT_PER_TIME);
   }

   @Override
   public void totalSize(long size) {
      logger.setTotal(size / 1024);
      final int len = String.valueOf(logger.getTotal()).length();
      logger.setFormat("Downloaded %1$" + len + "d/%5$d kb [%6$4.1f%%] at %2$.2f kb/s (%4$.2f kb/s for last %3$d kb)");
      kb = 0;
      logger.startTimer();
   }

   @Override
   public void progress(final long doneKB) {
      logger.stopTimerAndIncrement((int) ((doneKB - kb) / 1024));
      kb = doneKB;
      logger.startTimer();
   }

   @Override
   public void done() {
      logger.log();
   }
}
