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
public enum PipelineStepStatusCode {
   CONTINUE(PipelineStatusCode.CONTINUE, false),
   DIVERT_PIPELINE(PipelineStatusCode.CONTINUE, true),
   OVERRIDE_PIPELINE(PipelineStatusCode.FINISH, true),
   FINISH(PipelineStatusCode.FINISH, false);

   private final PipelineStatusCode statusCode;
   private final boolean subPipeline;

   PipelineStepStatusCode(PipelineStatusCode statusCode, boolean subPipeline) {
      this.statusCode = statusCode;
      this.subPipeline = subPipeline;
   }

   public boolean isDone() {
      return statusCode.isDone();
   }

   public boolean hasSubPipeline() {
      return subPipeline;
   }
   
   public PipelineStatusCode toPipelineStatusCode() {
      return statusCode;
   }
}
