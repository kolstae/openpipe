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


import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class PipelineTest extends TestCase {
   private Pipeline pipeline;
   private List<PipelineStep> steps = new ArrayList<PipelineStep>();
   private List<PipelineExceptionListener> eListeners = new ArrayList<PipelineExceptionListener>();
   private List<Document> docs = new ArrayList<Document>();
   private PipelineStep pipelineStep;
   private PipelineStep failingStep;

   @Override
   protected void setUp() throws Exception {
      pipeline = new Pipeline();
      pipeline.setPipelineSteps(steps);
      eListeners.add(new LoggingPipelineExceptionListener());
      docs.add(new Document());

      pipelineStep = createMock(PipelineStep.class);
      failingStep = createMock(PipelineStep.class);

      steps.add(pipelineStep);
      steps.add(failingStep);
   }

   public void testPrepare() throws Exception {
      PipelineStep pipelineStep = createMock(PipelineStep.class);
      PipelineStep failingStep = createMock(PipelineStep.class);
      PipelineExceptionHandler eHandler = createMock(PipelineExceptionHandler.class);

      steps.add(pipelineStep);
      steps.add(failingStep);

      // Testing one ok, and one failing step
      pipelineStep.prepare();
      failingStep.prepare();
      expectLastCall().andThrow(new RuntimeException("I am always failing"));
      expect(failingStep.getName()).andReturn("FailStep").anyTimes();

      replay(pipelineStep, failingStep);
      assertFalse("One step failed, success should be false", pipeline.prepare());
      verify(pipelineStep, failingStep);

      // Testing one ok, and one failing step with custom exceptionHandler
   }


   public void testExecute() throws Exception {
      Document doc = new Document();

      // Testing one ok, and one failing step
      pipelineStep.prepare();
      failingStep.prepare();
      expect(pipelineStep.execute(doc)).andReturn(PipelineStepStatus.DEFAULT);
      expect(failingStep.execute(doc)).andThrow(new RuntimeException("I am always failing"));

      expect(failingStep.getName()).andReturn("testFailStep").anyTimes();
      expect(pipelineStep.getName()).andReturn("testStep").anyTimes();
      expect(pipelineStep.getRevision()).andReturn("$Revision$").anyTimes();
      expect(failingStep.getRevision()).andReturn("$Revision$").anyTimes();

      replay(pipelineStep, failingStep);
      assertTrue(pipeline.prepare());
      PipelineFlow pipelineFlow = pipeline.execute(doc);
      assertFalse("One step failed, success should be false", pipelineFlow.isSuccess());
      assertTrue("One step failed, stop the pipeline execution", pipelineFlow.isStopPipeline());
      verify(pipelineStep, failingStep);
   }


   public void testSetPipelineExceptionHandler() throws Exception {
      PipelineExceptionHandler eHandler = createMock(PipelineExceptionHandler.class);

      pipeline.setPipelineExceptionHandler(eHandler);
      pipelineStep.prepare();
      failingStep.prepare();
      expectLastCall().andThrow(new RuntimeException("I am always failing"));
      expect(failingStep.getName()).andReturn("FailStep").anyTimes();
      expect(eHandler.handlePrepareException((PipelineException) notNull())).andReturn(PipelineFlowEnum.CONTINUE);
      replay(pipelineStep, failingStep, eHandler);

      assertTrue("Custom exception handler indicates that this is a success, but prepare returns false", pipeline.prepare());
      verify(eHandler, pipelineStep, failingStep);

      try {
         pipeline.setPipelineExceptionHandler(null);
         fail("Setting pipelineException handler should throw nullpointer");
      } catch (NullPointerException e) {
         // Everything ok.
      }
   }

   public void testFinish() throws Exception {
      PipelineExceptionHandler eHandler = createMock(PipelineExceptionHandler.class);

      // Testing a successful finish
      pipeline.setPipelineExceptionHandler(eHandler);

      failingStep.prepare();
      pipelineStep.prepare();
      failingStep.finish(true);
      pipelineStep.finish(true);

      expect(failingStep.getName()).andReturn("testFailStep").anyTimes();
      expect(pipelineStep.getName()).andReturn("testStep").anyTimes();
      expect(pipelineStep.getRevision()).andReturn("$Revision$").anyTimes();
      expect(failingStep.getRevision()).andReturn("$Revision$").anyTimes();

      replay(pipelineStep, failingStep, eHandler);

      pipeline.prepare();
      pipeline.finish(true);

      verify(pipelineStep, failingStep, eHandler);

      reset(pipelineStep, failingStep, eHandler);

      // testing a finish with the first step failing.
      failingStep.prepare();
      pipelineStep.prepare();
      failingStep.finish(true);
      expectLastCall().andThrow(new RuntimeException("I am always failing"));
      pipelineStep.finish(true);
      eHandler.handleFinishException((PipelineException) notNull());

      expect(failingStep.getName()).andReturn("testFailStep").anyTimes();
      expect(pipelineStep.getName()).andReturn("testStep").anyTimes();
      expect(pipelineStep.getRevision()).andReturn("$Revision$").anyTimes();
      expect(failingStep.getRevision()).andReturn("$Revision$").anyTimes();

      replay(pipelineStep, failingStep, eHandler);

      pipeline.prepare();
      pipeline.finish(true);

      verify(pipelineStep, failingStep, eHandler);
   }
}
