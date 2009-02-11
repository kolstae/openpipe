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
import no.trank.openpipe.config.annotation.NotNull;

/**
 * An abstract {@link PipelineStep} that selects a sub-pipeline based on the switch value implemented by a subclass
 * 
 * @version $Revision$
 */
public abstract class PipelineSelector extends BasePipelineStep {
   private static final Logger log = LoggerFactory.getLogger(PipelineSelector.class);
   private Map<String, List<PipelineStep>> switchMap = Collections.emptyMap();
   @NotNull
   private Map<String, PipelineStepStatusCode> statusCodeMap = Collections.emptyMap();
   private Map<String, SubPipeline> swMap = new HashMap<String, SubPipeline>();

   public PipelineSelector() {
   }

   public PipelineSelector(String name) {
      super(name);
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final String switchValue = getSwitchValue(doc);
      final SubPipeline pipeline = swMap.get(switchValue);
      final PipelineStepStatus status;
      if (pipeline != null) {
         status = handleSubPipeline(doc, pipeline);
      } else {
         final PipelineStepStatusCode statusCode = getStatusCode(switchValue, CONTINUE);
         if (statusCode.hasSubPipeline()) {
            throw new PipelineException("No sub-pipeline configured for operation '" + switchValue +
                  "' but code " + statusCode + " found");
         }
         status = new PipelineStepStatus(statusCode);
      }
      log.debug("Operation {}: {}", switchValue, status);
      return status;
   }

   protected abstract String getSwitchValue(Document doc);

   @Override
   public void prepare() throws PipelineException {
      super.prepare();

      swMap.clear();
      for (Map.Entry<String,List<PipelineStep>> entry : switchMap.entrySet()) {
         final BaseSubPipeline pipeline = new BaseSubPipeline(entry.getValue());
         pipeline.prepare();
         swMap.put(entry.getKey(), pipeline);
      }
   }

   @Override
   public void finish(boolean success) throws PipelineException {
      // Rethrow all exceptions
      final MultiPipelineException exception = BaseSubPipeline.finish(swMap.values(), success);
      if (exception != null) {
         exception.setPipelineStepNameIfNull(getName());
         throw exception;
      }
   }

   private PipelineStepStatus handleSubPipeline(Document doc, SubPipeline pipeline) {
      final PipelineStepStatusCode statusCode = getStatusCode(getSwitchValue(doc), DIVERT_PIPELINE);
      final PipelineStepStatus status = new PipelineStepStatus(statusCode, pipeline);
      if (!statusCode.hasSubPipeline()) {
         log.warn("Sub-pipeline for operation {} found, but status {} is set", getSwitchValue(doc), statusCode);
      }
      return status;
   }

   private PipelineStepStatusCode getStatusCode(String operation, PipelineStepStatusCode defaultCode) {
      final PipelineStepStatusCode code = statusCodeMap.get(operation);
      return code != null ? code : defaultCode;
   }

   public Map<String, List<PipelineStep>> getSwitchMap() {
      return switchMap;
   }

   public void setSwitchMap(Map<String, List<PipelineStep>> switchMap) {
      this.switchMap = switchMap;
   }

   public Map<String, PipelineStepStatusCode> getStatusCodeMap() {
      return statusCodeMap;
   }

   public void setStatusCodeMap(Map<String, PipelineStepStatusCode> statusCodeMap) {
      this.statusCodeMap = statusCodeMap;
   }
}
