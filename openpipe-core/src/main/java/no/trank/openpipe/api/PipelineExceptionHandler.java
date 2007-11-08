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
 * This class is a strategy for handling exceptions in the pipeline.
 *
 * <b>Note:</b> All implementations of this should notify exception listeners
 *
 *
 * @see no.trank.openpipe.api.DefaultPipelineExceptionHandler
 * @see no.trank.openpipe.api.BasePipelineExceptionHandler
 *
 * @version $Revision$
 */
public interface PipelineExceptionHandler {
   /**
    * An exception was thrown in pipeline prepare.
    *
    * @param ex the exception that was thrown
    * @return what the pipeline should do about this
    */
   PipelineFlow handlePrepareException(PipelineException ex);

   /**
    * An exception was thrown in pipeline finish.
    *
    * @param ex the exception that was thrown
    */
   void handleFinishException(PipelineException ex);

   /**
    * An ecxeption was thrown by the producer iterable.
    *
    * @param ex the exception that was thrown.
    * @return what the pipeline should do about this
    */
   PipelineFlow handleProducerException(PipelineException ex);

   /**
    * An exception was thrown on one document in the pipeline.
    *
    * @param ex the exception that was thrown.
    * @param document the document that triggered the exception.
    * @return what the pipeline should do about this
    */
   PipelineFlow handleDocumentException(PipelineException ex, Document document);

   /**
    * Add an exception listener that should be notified on every exception generated.
    *
    * @param exceptionListener the exception listener taht will be notified.
    */
   void addExceptionListener(PipelineExceptionListener exceptionListener);


   /**
    * Remove exception listener from the list that is notified on exception
    *
    * @param exceptionListener the listener to remove
    */
   void removeExceptionListener(PipelineExceptionListener exceptionListener);

}
