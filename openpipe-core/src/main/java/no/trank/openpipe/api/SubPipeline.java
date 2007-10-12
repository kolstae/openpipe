package no.trank.openpipe.api;

import java.util.List;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision: 874 $
 */
public interface SubPipeline {
   boolean prepare() throws PipelineException;
   
   void finish(boolean success) throws PipelineException;
   
   List<? extends PipelineStep> getPipelineSteps();

   void setPipelineSteps(List<? extends PipelineStep> pipelineSteps);

   PipelineStatusCode executeSteps(Document document) throws PipelineException;
}
