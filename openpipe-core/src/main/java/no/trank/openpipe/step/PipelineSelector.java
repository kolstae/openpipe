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
package no.trank.openpipe.step;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.BaseSubPipeline;
import no.trank.openpipe.api.MultiPipelineException;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStep;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import static no.trank.openpipe.api.PipelineStepStatusCode.CONTINUE;
import static no.trank.openpipe.api.PipelineStepStatusCode.DIVERT_PIPELINE;
import no.trank.openpipe.api.SubPipeline;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;

/**
 * A {@link PipelineStep} that selects a sub-pipeline and 
 * 
 * @version $Revision$
 */
public class PipelineSelector extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(PipelineSelector.class);
   @NotEmpty
   private Map<String, List<PipelineStep>> operationMap = Collections.emptyMap();
   @NotNull
   private Map<String, PipelineStepStatusCode> statusCodeMap = Collections.emptyMap();
   private Map<String, SubPipeline> opMap = new HashMap<String, SubPipeline>();

   public PipelineSelector() {
      super("PipelineSelector");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final SubPipeline pipeline = opMap.get(doc.getOperation());
      final PipelineStepStatus status;
      if (pipeline != null) {
         status = handleSubPipeline(doc, pipeline);
      } else {
         final PipelineStepStatusCode statusCode = getStatusCode(doc.getOperation(), CONTINUE);
         if (statusCode.hasSubPipeline()) {
            throw new PipelineException("No sub-pipeline configured for operation '" + doc.getOperation() + 
                  "' but code " + statusCode + " found");
         }
         status = new PipelineStepStatus(statusCode);
      }
      log.debug("Operation {}: {}", doc.getOperation(), status);
      return status;
   }

   @Override
   public void prepare() throws PipelineException {
      super.prepare();

      opMap.clear();
      for (Map.Entry<String,List<PipelineStep>> entry : operationMap.entrySet()) {
         final BaseSubPipeline pipeline = new BaseSubPipeline(entry.getValue());
         pipeline.prepare();
         opMap.put(entry.getKey(), pipeline);
      }
   }

   @Override
   public void finish(boolean success) throws PipelineException {
      // Rethrow all exceptions
      MultiPipelineException pipelineException = null;
      for (SubPipeline pipeline : opMap.values()) {
            try {
               pipeline.finish(success);
            } catch (PipelineException e) {
               if (pipelineException == null) {
                  pipelineException = new MultiPipelineException(getName());
               }
               pipelineException.add(e);
            } catch (RuntimeException e) {
               if (pipelineException == null) {
                  pipelineException = new MultiPipelineException(getName());
               }
               pipelineException.add(new PipelineException(e));
            }
      }
      opMap.clear();
      if (pipelineException != null) {
         throw pipelineException;
      }
   }

   private PipelineStepStatus handleSubPipeline(Document doc, SubPipeline pipeline) {
      final PipelineStepStatusCode statusCode = getStatusCode(doc.getOperation(), DIVERT_PIPELINE);
      final PipelineStepStatus status = new PipelineStepStatus(statusCode, pipeline);
      if (!statusCode.hasSubPipeline()) {
         log.warn("Sub-pipeline for operation {} found, but status {} is set", doc.getOperation(), statusCode);
      }
      return status;
   }

   private PipelineStepStatusCode getStatusCode(String operation, PipelineStepStatusCode defaultCode) {
      final PipelineStepStatusCode code = statusCodeMap.get(operation);
      return code != null ? code : defaultCode;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public Map<String, List<PipelineStep>> getOperationMap() {
      return operationMap;
   }

   public void setOperationMap(Map<String, List<PipelineStep>> operationMap) {
      this.operationMap = operationMap;
   }

   public Map<String, PipelineStepStatusCode> getStatusCodeMap() {
      return statusCodeMap;
   }

   public void setStatusCodeMap(Map<String, PipelineStepStatusCode> statusCodeMap) {
      this.statusCodeMap = statusCodeMap;
   }
}
