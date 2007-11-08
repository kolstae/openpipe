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
package no.trank.openpipe.jdbc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static no.trank.openpipe.api.document.DocumentOperation.*;

/**
 * @version $Revision$
 */
public class HtmlJdbcStats implements JdbcStats {
   private static final DateFormat DF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
   private Timer last = new Timer();
   private Timer total = new Timer(System.currentTimeMillis());
   private int totalRuns;

   @Override
   public void startPreSql() {
      last.startPreSql();
   }

   @Override
   public void startPostSql() {
      last.startPostSql();
   }

   @Override
   public void startFailSql() {
      last.startFailSql();
   }

   @Override
   public void stop() {
      last.stopPostSql();
      last.stopFailSql();
      total.add(last);
      ++totalRuns;
   }

   @Override
   public void incr(String operation) {
      if (ADD_VALUE.equals(operation)) {
         last.incrAddCount();
      } else if (MODIFY_VALUE.equals(operation)) {
         last.incrModifyCount();
      } else if (DELETE_VALUE.equals(operation)) {
         last.incrDeleteCount();
      } else {
         last.incrOtherCount();
      }
   }

   @Override
   public void startIt() {
      last.start();
   }

   public String getHtml() {
      StringBuilder ret = new StringBuilder(256);

      ret.append("<table cellpadding=\"3\" border=\"0\">")
            .append("  <tr>")
            .append("    <td></td><td>Last run</td><td>Total</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>Start</td>")
            .append("    <td>").append(last.getStart()).append("</td>")
            .append("    <td>").append(total.getStart()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>Query Time</td>")
            .append("    <td>").append(last.getTime()).append("</td>")
            .append("    <td>").append(total.getTime()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>Docs/sec</td>")
            .append("    <td>").append(last.getTimePerDoc()).append("</td>")
            .append("    <td>").append(total.getTimePerDoc()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>PreSQL Time</td>")
            .append("    <td>").append(last.getPreSqlTime()).append("</td>")
            .append("    <td>").append(total.getPreSqlTime()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>PostSQL Time</td>")
            .append("    <td>").append(last.getPostSqlTime()).append("</td>")
            .append("    <td>").append(total.getPostSqlTime()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>FailSQL Time</td>")
            .append("    <td>").append(last.getFailSqlTime()).append("</td>")
            .append("    <td>").append(total.getFailSqlTime()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>Add</td>")
            .append("    <td>").append(last.getAddCount()).append("</td>")
            .append("    <td>").append(total.getAddCount()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>Modify</td>")
            .append("    <td>").append(last.getModifyCount()).append("</td>")
            .append("    <td>").append(total.getModifyCount()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>Delete</td>")
            .append("    <td>").append(last.getDeleteCount()).append("</td>")
            .append("    <td>").append(total.getDeleteCount()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td>Other</td>")
            .append("    <td>").append(last.getOtherCount()).append("</td>")
            .append("    <td>").append(total.getOtherCount()).append("</td>")
            .append("  </tr>")
            .append("  <tr>")
            .append("    <td></td>")
            .append("    <td></td>")
            .append("    <td>").append(totalRuns).append(" run").append(totalRuns == 1 ? "" : "s").append("</td>")
         .append("  </tr>")
         .append("</table>");
      
      
      return ret.toString();
   }

   private static class Timer {
      private long start;
      private long time;
      private int otherCount;
      private int addCount;
      private int modifyCount;
      private int deleteCount;
      private long preSqlStart;
      private long preSqlTime;
      private long postSqlStart;
      private long postSqlTime;
      private long failSqlStart;
      private long failSqlTime;

      private Timer() {
      }

      public Timer(long start) {
         this.start = start;
      }

      public void start() {
         final long now = System.currentTimeMillis();
         preSqlTime = now - preSqlStart;
         start = now;
      }
      
      public String getStart() {
         return start == 0 ? "" : DF.format(start);
      }

      public void startPreSql() {
         preSqlTime = -1;
         preSqlStart = 0;
         postSqlStart = 0;
         postSqlTime = -1;
         failSqlStart = 0;
         failSqlTime = -1;
         addCount = 0;
         modifyCount = 0;
         deleteCount = 0;
         time = -1;
         preSqlStart = System.currentTimeMillis();
      }

      public void startPostSql() {
         final long now = System.currentTimeMillis();
         time = now - start;
         postSqlStart = now;
      }

      public void startFailSql() {
         final long now = System.currentTimeMillis();
         time = now - start;
         failSqlStart = now;
      }

      public void stopPostSql() {
         postSqlTime = System.currentTimeMillis() - postSqlStart;
      }

      public void stopFailSql() {
         failSqlTime = System.currentTimeMillis() - failSqlStart;
      }

      public String getPreSqlTime() {
         return calcTimeUsed(preSqlTime, preSqlStart);
      }

      public String getPostSqlTime() {
         return calcTimeUsed(postSqlTime, postSqlStart);
      }

      private static long calcTime(final long time, final long start) {
         if (time > 0) {
            return time;
         }
         return start == 0 ? -1 : System.currentTimeMillis() - start;
      }

      public String getTime() {
         return calcTimeUsed(this.time, start);
      }

      private static String calcTimeUsed(final long timeUsed, final long start) {
         final long time = calcTime(timeUsed, start);
         if (time > 0) {
            return new StringBuilder().append(time).append(" ms").toString();
         }
         return "";
      }

      public String getTimePerDoc() {
         final int count = addCount + modifyCount + deleteCount + otherCount;
         if (count > 0) {
            final long time = calcTime(this.time, start);
            if (time > 0) {
               return new StringBuilder().append(count * 1000 / time).append(" docs/sec").toString();
            }
         }
         return "";
      }

      public int getOtherCount() {
         return otherCount;
      }

      public int getAddCount() {
         return addCount;
      }

      public int getModifyCount() {
         return modifyCount;
      }

      public int getDeleteCount() {
         return deleteCount;
      }

      public void incrOtherCount() {
         otherCount++;
      }

      public void incrAddCount() {
         addCount++;
      }

      public void incrModifyCount() {
         modifyCount++;
      }

      public void incrDeleteCount() {
         deleteCount++;
      }

      public long getFailSqlTime() {
         return failSqlTime;
      }

      public void add(Timer timer) {
         time += timer.time;
         otherCount += timer.otherCount;
         addCount += timer.addCount;
         modifyCount += timer.modifyCount;
         deleteCount += timer.deleteCount;
         preSqlTime += timer.preSqlTime;
         postSqlTime += timer.postSqlTime;
      }

   }
}
