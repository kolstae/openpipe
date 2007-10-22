package no.trank.openpipe.api;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import org.easymock.IMocksControl;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class BaseSubPipelineTest extends TestCase {

   public void testExecuteSteps() throws Exception {
      final Document doc1 = createDocument();
      final Document doc2 = createDocument();
      final IMocksControl mc = createStrictControl();
      mc.checkOrder(false);
      final List<PipelineStep> steps = createStepMock(mc, 3, 2, doc1, doc2);
      mc.replay();
      final BaseSubPipeline pipeline = new BaseSubPipeline(steps);
      pipeline.prepare();
      pipeline.executeSteps(doc1);
      pipeline.executeSteps(doc2);
      pipeline.finish(true);
      pipeline.prepare();
      pipeline.executeSteps(doc1);
      pipeline.executeSteps(doc2);
      pipeline.finish(true);
      mc.verify();
   }

   public void testPartialPrepare() throws Exception {
      final IMocksControl mc = createStrictControl();
      mc.checkOrder(false);
      final List<PipelineStep> steps = createStepMock(mc, 3, 1, (Document[]) null);
      final PipelineStep step = mc.createMock(PipelineStep.class);
      step.prepare();
      expectLastCall().andThrow(new PipelineException("Failed to prepare"));
      expect(step.getName()).andReturn("Step Fail").anyTimes();
      expect(step.getRevision()).andReturn("$Revision: Fail$").anyTimes();
      steps.add(step);
      steps.add(mc.createMock(PipelineStep.class));
      mc.replay();
      final BaseSubPipeline pipeline = new BaseSubPipeline(steps);
      try {
         pipeline.prepare();
         fail("Should throw exception");
      } catch (PipelineException e) {
         // Ignoring
      }
      pipeline.finish(true);
      mc.verify();
   }

   private static List<PipelineStep> createStepMock(IMocksControl mc, int count, int runs, Document... docs)
         throws PipelineException {
      final ArrayList<PipelineStep> list = new ArrayList<PipelineStep>(count + 2);
      for (int i = 0; i < count; i++) {
         final PipelineStep ps = mc.createMock(PipelineStep.class);
         ps.prepare();
         expectLastCall().times(runs);
         expect(ps.getName()).andReturn("Step " + i).anyTimes();
         expect(ps.getRevision()).andReturn("$Revision: " + i + '$').anyTimes();
         if (docs != null) {
            for (Document doc : docs) {
               expect(ps.execute(doc)).andReturn(new PipelineStepStatus(PipelineStepStatusCode.CONTINUE)).times(runs);
            }
         }
         ps.finish(true);
         expectLastCall().times(runs);
         list.add(ps);
      }
      return list;
   }

   private static Document createDocument() {
      final Document doc1 = new Document();
      doc1.setOperation("test");
      return doc1;
   }
}
