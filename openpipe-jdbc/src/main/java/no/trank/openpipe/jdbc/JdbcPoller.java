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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @version $Revision$
 */
public class JdbcPoller implements Runnable {
   private static final Logger log = LoggerFactory.getLogger(JdbcPoller.class);
   
   private Runnable pipelineRunner;
   private boolean shutdown;
   private long intervalMillis = 1000L;
   private Thread thread;
   
   public boolean isRunning() {
      return thread != null;
   }
   
   public boolean isInShutdown() {
      return thread != null && shutdown;
   }
   
   public boolean start() {
      if(thread == null) {
         shutdown = false;
         thread = new Thread(this);
         thread.setDaemon(true);
         thread.start();
         return true;
      }
      return false;
   }
   
   public boolean stop() {
      if(thread != null && !shutdown) {
         shutdown = true;
         return true;
      }
      return false;
   }
   
   @Override
   public void run() {
      while(!shutdown) {
         log.info("polling database");
         long now = System.currentTimeMillis();
         pipelineRunner.run();
         log.info("polling database done - {} ms", (System.currentTimeMillis() - now));
         
         if(shutdown || intervalMillis <= 0) {
            shutdown = true;
         }
         else {
            try {
               Thread.sleep(intervalMillis);
            } catch(InterruptedException e) {
            }
         }
      }
      thread = null;
   }
   
   public void setIntervalMillis(long intervalMillis) {
      this.intervalMillis = intervalMillis;
   }

   // spring setters
   public void setPipelineRunner(Runnable pipelineRunner) {
      this.pipelineRunner = pipelineRunner;
   }
}