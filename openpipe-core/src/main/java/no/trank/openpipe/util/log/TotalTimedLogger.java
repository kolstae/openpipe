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
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

/**
 * @version $Revision$
 */
public class TotalTimedLogger extends DefaultTimedLogger {
   private long total;

   public TotalTimedLogger() {
   }

   public TotalTimedLogger(Logger log, String format) {
      super(log, format);
   }

   public TotalTimedLogger(Logger log, String format, TimeUnit timeUnit) {
      super(log, format, timeUnit);
   }

   public TotalTimedLogger(Logger log, String format, TimeUnit timeUnit, Calculator calculator) {
      super(log, format, timeUnit, calculator);
   }

   @Override
   protected void format(Formatter formatter, String format, long totCount, long totNanos, int localCount, long localTotNanos) {
      formatter.format(format, totCount, calculateAverage(totNanos, (double) totCount),
            localCount, calculateAverage(localTotNanos, (double) localCount), total, totCount * 100.0 / total);
   }

   public long getTotal() {
      return total;
   }

   public void setTotal(long total) {
      this.total = total;
   }
}
