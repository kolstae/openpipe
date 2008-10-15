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
import static java.util.concurrent.TimeUnit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A timed logger that logs with a given interval.
 *
 * @version $Revision$
 */
public class DefaultTimedLogger implements TimedLogger {
   private final StringBuilder buf = new StringBuilder(64);
   private final Formatter formatter = new Formatter(buf);
   private Logger log;
   private String format;
   private long count;
   private int localCount;
   private long start;
   private long localTot;
   private long tot;
   private long lastLog = System.nanoTime();
   private long logPeriod = SECONDS.toNanos(20);
   protected final TimeUnit timeUnit;
   protected final Calculator calculator;

   /**
    * Creates a timed logger.
    *
    * @see #setLog(Logger)
    * @see #setFormat(String)
    * @see #setLogPeriodInSeconds(long)
    */
   public DefaultTimedLogger() {
      this(LoggerFactory.getLogger(DefaultTimedLogger.class), "%1$d (%3$d) ops at %2$.2f (%4$.2f) ms/op");
   }

   /**
    * Creates a timed logger with the given logger and format.
    *
    * @param log the logger to use.
    * @param format the format to use.
    */
   public DefaultTimedLogger(Logger log, String format) {
      this(log, format, MILLISECONDS);
   }

   /**
    * Creates a timed logger with the given logger and format.
    *
    * @param log the logger to use.
    * @param format the format to use.
    * @param timeUnit the time unit used for averages and times.
    */
   public DefaultTimedLogger(Logger log, String format, TimeUnit timeUnit) {
      this(log, format, timeUnit, Calculator.TIME_PER_UNIT);
   }

   /**
    * Creates a timed logger with the given logger and format.
    *
    * @param log the logger to use.
    * @param format the format to use.
    * @param timeUnit the time unit used for averages and times.
    */
   public DefaultTimedLogger(Logger log, String format, TimeUnit timeUnit, Calculator calculator) {
      this.log = log;
      this.format = format;
      this.timeUnit = timeUnit;
      this.calculator = calculator;
   }

   @Override
   public void startTimer() {
      start = System.nanoTime();
   }

   /**
    * {@inheritDoc}
    * <p>Logs info if time since last log exceeds {@link #getLogPeriodInSeconds()}</p>
    */
   @Override
   public void stopTimerAndIncrement() {
      stopTimerAndIncrement(1);
   }

   @Override
   public void stopTimerAndIncrement(final int byCount) {
      final long now = System.nanoTime();
      final long delta = now - start;
      localTot += delta;
      tot += delta;
      count += byCount;
      localCount += byCount;
      if (now - lastLog > logPeriod) {
         log();
         lastLog = now;
         localTot = 0;
         localCount = 0;
      }
   }

   @Override
   public void log() {
      if (count > 0 && log.isInfoEnabled()) {
         format(formatter, format, count, tot, localCount, localTot);
         log.info(buf.toString());
         buf.setLength(0);
      }
   }

   protected void format(final Formatter formatter, final String format, final long totCount, final long totNanos,
                         final int localCount, final long localTotNanos) {
      formatter.format(format, totCount, calculateAverage(totNanos, (double) totCount),
            localCount, calculateAverage(localTotNanos, (double) localCount));
   }

   protected double calculateAverage(final long totNanos, final double count) {
      return calculator.calculate(timeUnit.convert(totNanos, NANOSECONDS), count);
   }

   @Override
   public void reset() {
      count = 0;
      tot = 0;
      localCount = 0;
      localTot = 0;
      lastLog = System.nanoTime();
   }

   /**
    * Gets the logger used for logging.
    *
    * @return the logger used for logging.
    */
   public Logger getLog() {
      return log;
   }

   /**
    * Sets the logger used for logging. Default <tt>LoggerFactory.getLogger(DefaultTimedLogger.class)</tt>.
    *
    * @param log the logger used for logging. <b>Cannot</b> be <tt>null</tt>.
    */
   public void setLog(Logger log) {
      this.log = log;
   }

   /**
    * Gets the format of the log statement.
    *
    * @return the format of the log statement.
    *
    * @see #setFormat(String)
    */
   public String getFormat() {
      return format;
   }

   /**
    * Sets the format of the log statement. Default <tt>&quot;%1$d (%2$d) ops at %2$.2f (%4$.2f) ms/op&quot;</tt>.
    *
    * @param format the format of the log statement.
    *
    * @see Formatter
    */
   public void setFormat(String format) {
      this.format = format;
   }

   /**
    * Gets the log period in seconds.
    *
    * @return the log period in seconds.
    */
   public long getLogPeriodInSeconds() {
      return NANOSECONDS.toSeconds(logPeriod);
   }

   /**
    * Sets the log period in seconds. Default is <tt>20</tt> seconds.
    *
    * @param logPeriod the log period in seconds.
    */
   public void setLogPeriodInSeconds(long logPeriod) {
      setLogPeriod(logPeriod, SECONDS);
   }

   /**
    * Gets the log period in nanos.
    *
    * @return the log period in nanos.
    */
   public long getLogPeriod() {
      return logPeriod;
   }

   /**
    * Sets the log period in nanos.
    *
    * @param logPeriod the log period in nanos.
    */
   public void setLogPeriod(long logPeriod) {
      this.logPeriod = logPeriod;
   }

   /**
    * Sets the log period in the given <tt>TimeUnit</tt>.
    *
    * @param logPeriod the log period.
    * @param unit the <tt>TimeUnit</tt> of <tt>logPeriod</tt>.
    */
   public void setLogPeriod(long logPeriod, TimeUnit unit) {
      setLogPeriod(unit.toNanos(logPeriod));
   }

   public static enum Calculator {
      TIME_PER_UNIT {
         @Override
         public double calculate(final long totTime, final double count) {
            return totTime / count;
         }
      },
      UNIT_PER_TIME {
         @Override
         public double calculate(final long totTime, final double count) {
            return count / totTime;
         }
      };

      public abstract double calculate(final long totTime, final double count);
   }
}