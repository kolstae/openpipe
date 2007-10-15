package no.trank.openpipe.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class BaseSubPipeline implements SubPipeline {
   private static final Logger log = LoggerFactory.getLogger(BaseSubPipeline.class);
   private List<? extends PipelineStep> pipelineSteps;

   public BaseSubPipeline() {
   }

   public BaseSubPipeline(List<? extends PipelineStep> pipelineSteps) {
      this.pipelineSteps = pipelineSteps;
   }

   public List<? extends PipelineStep> getPipelineSteps() {
      return pipelineSteps;
   }

   public void setPipelineSteps(List<? extends PipelineStep> pipelineSteps) {
      this.pipelineSteps = pipelineSteps;
   }

   public boolean prepare() throws PipelineException {
      for (PipelineStep step : getPipelineSteps()) {
         try {
            step.prepare();
         } catch (PipelineException e) {
            e.setPipelineStepNameIfNull(step.getName());
            throw e;
         }
      }
      return true;
   }

   public void finish(boolean success) throws PipelineException {
      MultiPipelineException pipelineException = null;
      for (PipelineStep step : getPipelineSteps()) {
         try {
            step.finish(success);
         } catch (PipelineException e) {
            e.setPipelineStepNameIfNull(step.getName());
            if (pipelineException == null) {
               pipelineException = new MultiPipelineException();
            }
            pipelineException.add(e);
         } catch (RuntimeException e) {
            if (pipelineException == null) {
               pipelineException = new MultiPipelineException();
            }
            pipelineException.add(new PipelineException(e, step.getName()));
         }
      }
      if (pipelineException != null) {
         throw pipelineException;
      }
   }

   public PipelineStatusCode executeSteps(Document document) throws PipelineException {
      PipelineStatusCode pipelineStatusCode = PipelineStatusCode.CONTINUE;
      for (PipelineStep pipelineStep : pipelineSteps) {
         final String infoString = buildPipelineInfo(document, pipelineStep);
         log.debug("Running {}", infoString);
         final PipelineStepStatusCode stepStatusCode;
         try {
            final long start = System.currentTimeMillis();
            PipelineStepStatus status = pipelineStep.execute(document);
            log.info("Execute {} took {} millis", infoString, System.currentTimeMillis() - start);
            if (status == null) {
               throw new PipelineException(pipelineStep.getName() + " returned null status", pipelineStep.getName());
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

   private static String buildPipelineInfo(Document document, PipelineStep pipelineStep) {
      return pipelineStep.getName() + pipelineStep.getRevision().replace('$', ' ') + "document operation: " + document.getOperation();
   }
}
