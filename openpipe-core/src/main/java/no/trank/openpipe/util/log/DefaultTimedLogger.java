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
package no.trank.openpipe.util.log;

import java.util.Formatter;
import static java.util.concurrent.TimeUnit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision$
 */
public class DefaultTimedLogger implements TimedLogger {
   private final StringBuilder buf = new StringBuilder(64);
   private final Formatter formatter = new Formatter(buf);
   private Logger log;
   private String format;
   private int count;
   private long start;
   private long tot;
   private long lastLog = System.nanoTime();
   private long logPeriod = SECONDS.toNanos(10);

   public DefaultTimedLogger() {
      this(LoggerFactory.getLogger(DefaultTimedLogger.class), "%1$d operations at %2$.2f millis/operation");
   }

   public DefaultTimedLogger(Logger log, String format) {
      this.log = log;
      this.format = format;
   }

   @Override
   public void startTimer() {
      start = System.nanoTime();
   }

   @Override
   public void stopTimerAndIncrement() {
      final long now = System.nanoTime();
      tot += now - start;
      count++;
      if (now - lastLog > logPeriod) {
         log();
         lastLog = now;
      }
   }

   @Override
   public void log() {
      if (count > 0 && log.isInfoEnabled()) {
         formatter.format(format, count, calculateAverage(tot, (double) count));
         log.info(buf.toString());
         buf.setLength(0);
      }
   }

   protected double calculateAverage(long totNanos, double count) {
      return MILLISECONDS.convert(totNanos, NANOSECONDS) / count;
   }

   @Override
   public void reset() {
      count = 0;
      tot = 0;
      lastLog = System.nanoTime();
   }

   public Logger getLog() {
      return log;
   }

   public void setLog(Logger log) {
      this.log = log;
   }

   public String getFormat() {
      return format;
   }

   public void setFormat(String format) {
      this.format = format;
   }

   public long getLogPeriodInSeconds() {
      return NANOSECONDS.toSeconds(logPeriod);
   }

   public void setLogPeriodInSeconds(long logPeriod) {
      this.logPeriod = SECONDS.toNanos(logPeriod);
   }
}
