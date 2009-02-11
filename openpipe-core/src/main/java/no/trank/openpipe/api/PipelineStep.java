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

import no.trank.openpipe.api.document.Document;

/**
 * An instance of this class represents an atomic operation on a {@link Document}.
 * <p/>
 * Before a batch of documents is processed, {@link #prepare()} must be called. After a batch of documents has been
 * processed, {@link #finish(boolean)} must be called.
 *
 * @version $Revision$
 */
public interface PipelineStep extends Finishable {

   /**
    * This is called when the application wants a document processed by this step.
    *
    * @param doc the document to process.
    * 
    * @return a status telling what to do next. Usually <tt>PipelineStepStatus.DEFAULT</tt>
    * 
    * @throws PipelineException if the step fails execution.
    */
   PipelineStepStatus execute(Document doc) throws PipelineException;

   /**
    * Will be called before a batch of documents is executed.
    *
    * @throws PipelineException if prepare failed.
    */
   void prepare() throws PipelineException;

   /**
    * Will be called when a batch of documents has been executed.
    *
    * @param success <tt>true</tt> if the batch was successful.
    *
    * @throws PipelineException if finish failed.
    */
   @Override
   void finish(boolean success) throws PipelineException;

   /**
    * Returns the revision of this step.
    *
    * @return the revision of this step
    */
   String getRevision();

   /**
    * Gets the name of this step.
    * 
    * @return the name of this step.
    */
   String getName();

   /**
    * Sets the name of this step.
    * 
    * @param name the new name of this step.
    */
   void setName(String name);
}
