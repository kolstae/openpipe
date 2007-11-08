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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision$
 */
public class JdbcAdmin extends HttpServlet {
   private static final long serialVersionUID = 7503537802693274153L;

   private static final Logger log = LoggerFactory.getLogger(JdbcAdmin.class);
   
   private Server server;
   private JdbcPoller jdbcPoller;
   private HtmlJdbcStats jdbcStats;
   
   private boolean jettyEnabled;
   private boolean pollingEnabled;
   private long intervalMillis;

   
   // do not rename to init - would override servlet init
   public void start() {
      if(jettyEnabled) {
         startJetty();
      }
      if(pollingEnabled) {
         startPolling();
      }
   }
   
   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      ServletOutputStream out = resp.getOutputStream();
      
      StringBuilder sb = new StringBuilder();
      String cmd = req.getRequestURI().replaceAll("/", "");
      
      String status = "";
      if(cmd.equals("startpolling")) {
         status = startPolling();
      }
      else if(cmd.equals("stoppolling")) {
         status = stopPolling();
      }
      else if(cmd.equals("stopjetty")) {
         status = stopJetty();
      }
      
      sb.append(jdbcStats.getHtml());
      
      sb.append("<br/><br/>");
      
      if(jdbcPoller.isInShutdown()) {
         sb.append("Polling (in shutdown) --- <a href='startpolling'>Start</a> - Stop");
      }
      else if(jdbcPoller.isRunning()) {
         sb.append("Polling (running) --- Start - <a href='stoppolling'>Stop</a>");
      }
      else {
         sb.append("Polling (stopped) --- <a href='startpolling'>Start</a> - Stop");
      }
      
      sb.append("<br/><a href='stopjetty'>Stop jetty</a>");
      
      if(status != null && status.length() > 0) {
         sb.append("<br/><br>").append(status);
      }
      
      byte[] b = sb.toString().getBytes();
      
      resp.setContentType("text/html");
      resp.setContentLength(b.length);
      out.write(b);
      out.flush();
   }
   
   private String startPolling() {
      if(jdbcPoller.isInShutdown()) {
         return "Jdbc poller is in shutdown";
      }
      else if(jdbcPoller.isRunning()) {
         return "Jdbc poller is already running";
      }
      
      jdbcPoller.setIntervalMillis(intervalMillis);
      jdbcPoller.start();
      return "Starting jdbc poller on " + intervalMillis + " ms intervals";
   }
   
   private String stopPolling() {
      if(jdbcPoller.isInShutdown()) {
         return "Jdbc poller is in shutdown";
      }
      else if(!jdbcPoller.isRunning()) {
         return "Jdbc poller has already been stopped";
      }
      
      jdbcPoller.stop();
      return "Stopping jdbc poller";
   }
   
   private String startJetty() {
      String ret = "";
      try {
         if(!server.isRunning()) {
            ret += "Attempting to start jetty.";
            server.start();
            ret += "  - Success";
         }
         else {
            ret += "Jetty is already running.";
         }
      } catch (Exception e) {
         log.error("Error starting jetty", e);
         ret += "  - Error: " + e.toString();
      }
      
      return ret;
   }

   private String stopJetty() {
      String ret = "";
      try {
         if(server.isRunning()) {
            ret += "Attempting to stop jetty";
            server.stop();
            ret += "  - Success";
         }
         else {
            ret += "Jetty has already been stopped.";
         }
      } catch (Exception e) {
         log.warn("Error stopping jetty", e);
         ret += "  - Error: " + e.toString();
      }
      return ret;
   }

   // spring setters
   public void setIntervalMillis(long intervalMillis) {
      this.intervalMillis = intervalMillis;
   }

   public void setJdbcPoller(JdbcPoller jdbcPoller) {
      this.jdbcPoller = jdbcPoller;
   }

   public void setJettyEnabled(boolean jettyEnabled) {
      this.jettyEnabled = jettyEnabled;
   }

   public void setPollingEnabled(boolean pollingEnabled) {
      this.pollingEnabled = pollingEnabled;
   }

   public void setServer(Server server) {
      this.server = server;
   }

   public HtmlJdbcStats getJdbcStats() {
      return jdbcStats;
   }

   public void setJdbcStats(HtmlJdbcStats jdbcStats) {
      this.jdbcStats = jdbcStats;
   }
}
