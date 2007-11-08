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
 *
 * The pipelineflow is used to control flow at exceptions in the pipeline.
 *
 * @see no.trank.openpipe.api.PipelineFlowEnum
 * @see no.trank.openpipe.api.PipelineExceptionHandler
 *  
 * @version $Revision$
 */
public interface PipelineFlow {

   /**
    * Indicates if the pipeline operations should be stopped.(Aborted)
    * @return <tt>true</tt> to stop, false otherwise
    */
   public boolean isStopPipeline();

   /**
    * Indicates if the operation was a success. This will be used when calling <tt>finish(boolean)</tt> on the
    * pipeline-steps.
    * @return <tt>true</tt> if everything is ok, false otherwise
    */
   public boolean isSuccess();
}
