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

/**
 * @version $Revision$
 */
public class PipelineException extends Exception {
   private static final long serialVersionUID = -6173006561779823086L;
   private String pipelineStepName;

   public PipelineException() {
      super();
   }

   public PipelineException(String message) {
      this(message, null, null);
   }
   
   public PipelineException(String message, String pipelineStepName) {
      this(message, null, pipelineStepName);
   }

   public PipelineException(String message, Throwable cause) {
      this(message, cause, null);
   }
   
   public PipelineException(String message, Throwable cause, String pipelineStepName) {
      super(message, cause);
      this.pipelineStepName = pipelineStepName;
   }

   public PipelineException(Throwable cause) {
      this(null, cause, null);
   }
   
   public PipelineException(Throwable cause, String pipelineStepName) {
      this(null, cause, pipelineStepName);
   }

   public String getPipelineStepName() {
      return pipelineStepName;
   }

   public void setPipelineStepName(String pipelineStepName) {
      this.pipelineStepName = pipelineStepName;
   }

   public void setPipelineStepNameIfNull(String pipelineStepName) {
      if (this.pipelineStepName == null) {
         setPipelineStepName(pipelineStepName);
      }
   }

   @Override
   public String getMessage() {
      return pipelineStepName == null ? super.getMessage() : pipelineStepName + ": " + super.getMessage();
   }
}
