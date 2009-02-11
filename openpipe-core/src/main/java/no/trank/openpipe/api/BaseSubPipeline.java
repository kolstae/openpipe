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
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class BaseSubPipeline implements SubPipeline {
   private static final Logger log = LoggerFactory.getLogger(BaseSubPipeline.class);
   private final List<PipelineStep> preparedSteps = new ArrayList<PipelineStep>();
   private List<? extends PipelineStep> pipelineSteps;

   public BaseSubPipeline() {
   }

   public BaseSubPipeline(List<? extends PipelineStep> pipelineSteps) {
      setPipelineSteps(pipelineSteps);
   }

   @Override
   public List<? extends PipelineStep> getPipelineSteps() {
      return pipelineSteps;
   }

   @Override
   public void setPipelineSteps(List<? extends PipelineStep> pipelineSteps) {
      this.pipelineSteps = pipelineSteps;
   }

   @Override
   public boolean prepare() throws PipelineException {
      preparedSteps.clear();
      for (PipelineStep step : getPipelineSteps()) {
         try {
            step.prepare();
            log.info("Prepared {} {}", step.getName(), step.getRevision().replace('$', ' ').trim());
            preparedSteps.add(step);
         } catch (PipelineException e) {
            e.setPipelineStepNameIfNull(step.getName());
            throw e;
         } catch (RuntimeException e) {
            throw new PipelineException(e, step.getName());
         }
      }
      return true;
   }

   @Override
   public void finish(boolean success) throws PipelineException {
      final MultiPipelineException exception = finish(preparedSteps, success);
      if (exception != null) {
         throw exception;
      }
   }

   public static MultiPipelineException finish(Collection<? extends Finishable> steps, boolean success) {
      MultiPipelineException pipelineException = null;
      for (Finishable step : steps) {
         try {
            step.finish(success);
         } catch (MultiPipelineException e) {
            if (pipelineException == null) {
               pipelineException = e;
            } else {
               pipelineException.add(e.getCause());
            }
         } catch (PipelineException e) {
            if (pipelineException == null) {
               pipelineException = new MultiPipelineException();
            }
            pipelineException.add(e);
         } catch (RuntimeException e) {
            if (pipelineException == null) {
               pipelineException = new MultiPipelineException();
            }
            pipelineException.add(new PipelineException(e));
         }
      }
      steps.clear();
      return pipelineException;
   }

   @Override
   public PipelineStatusCode executeSteps(Document document) throws PipelineException {
      PipelineStatusCode pipelineStatusCode = PipelineStatusCode.CONTINUE;
      for (PipelineStep pipelineStep : preparedSteps) {
         final PipelineStepStatusCode stepStatusCode;
         try {
            PipelineStepStatus status = executeStep(document, pipelineStep);
            if (status == null) {
               throw new PipelineException("null status received", pipelineStep.getName());
            }
            stepStatusCode = status.getStatusCode();
            if (stepStatusCode.hasSubPipeline()) {
               pipelineStatusCode = status.getSubPipeline().executeSteps(document);
            } else {
               pipelineStatusCode = stepStatusCode.toPipelineStatusCode();
            }
            if (stepStatusCode.isDone()) {
               return stepStatusCode.toPipelineStatusCode();
            }
         } catch (PipelineException e) {
            e.setPipelineStepNameIfNull(pipelineStep.getName());
            throw e;
         } catch (RuntimeException e) {
            throw new PipelineException(e, pipelineStep.getName());
         }
      }
      return pipelineStatusCode;
   }

   protected PipelineStepStatus executeStep(Document document, PipelineStep pipelineStep) throws PipelineException {
      final String infoString;
      final long start;
      final boolean debug = log.isDebugEnabled();
      if (debug) {
         infoString = pipelineStep.getName() + " document operation: " + document.getOperation();
         log.debug("Running {}", infoString);
         start = System.currentTimeMillis();
      } else {
         infoString = null;
         start = 0;
      }

      final PipelineStepStatus status = pipelineStep.execute(document);

      if (debug) {
         log.debug("Execute {} took {} millis", infoString, System.currentTimeMillis() - start);
      }
      return status;
   }
}
