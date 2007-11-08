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
public enum PipelineStatusCode {
   /**
    * Signals <tt>continue</tt> to the pipeline. {@link PipelineStatusCode#isDone() CONTINUE.isDone()} <tt>== false</tt>
    */
   CONTINUE(false),

   /**
    * Signals <tt>finish</tt> to the pipeline. {@link PipelineStatusCode#isDone() FINISH.isDone()} <tt>== true</tt>
    */
   FINISH(true);

   private boolean done;

   PipelineStatusCode(boolean done) {
      this.done = done;
   }

   /**
    * Gets whether this status signals <tt>done</tt>.
    *
    * @return <tt>true</tt> if this status signals <tt>done</tt>, otherwise <tt>false</tt>.
    */
   public boolean isDone() {
      return done;
   }
}
