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
package no.trank.openpipe.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.Document;

/**
 * A simple error-handler that logs all exceptions.
 *
 * @version $Revision$
 */
public class LoggingPipelineExceptionListener implements PipelineExceptionListener {
   private static final Logger log = LoggerFactory.getLogger(LoggingPipelineExceptionListener.class);

   public void onException(PipelineException ex, Document document) {
      log.error("Exception thrown in '" + ex.getPipelineStepName() + "'", ex);
      if (document != null) {
         if (log.isDebugEnabled()) {
            log.error(document.toString());
         }
      }
   }
}
