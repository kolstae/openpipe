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
 * @version $Revision$
 */

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

import no.trank.openpipe.api.document.Document;


public class DefaultPipelineExceptionHandlerTest extends TestCase {
   DefaultPipelineExceptionHandler defaultPipelineExceptionHandler;

   @Override
   protected void setUp() throws Exception {
      defaultPipelineExceptionHandler = new DefaultPipelineExceptionHandler();
   }

   public void testHandleExceptions() throws Exception {
      PipelineExceptionListener exceptionListener = createMock(PipelineExceptionListener.class);
      defaultPipelineExceptionHandler.addExceptionListener(exceptionListener);
      PipelineException pe = new PipelineException("test", "testStep");
      Document doc = new Document();

      // Expecting calls
      exceptionListener.onException(pe, null);
      exceptionListener.onException(pe, null);
      exceptionListener.onException(pe, null);
      exceptionListener.onException(pe, doc);

      replay(exceptionListener);
      defaultPipelineExceptionHandler.handleProducerException(pe);
      defaultPipelineExceptionHandler.handlePrepareException(pe);
      defaultPipelineExceptionHandler.handleFinishException(pe);
      defaultPipelineExceptionHandler.handleDocumentException(pe, doc);
      verify(exceptionListener);
  }
}