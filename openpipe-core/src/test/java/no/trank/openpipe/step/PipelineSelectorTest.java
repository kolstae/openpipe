package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStep;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision:712 $
 */
public class PipelineSelectorTest extends TestCase {
   private static final String ADD = "ADD";
   private static final String DELETE = "DELETE";

   public void testMissconfigured() throws Exception {
      final PipelineSelector selector = new PipelineSelector();
      selector.setName("selector");
      final HashMap<String, PipelineStepStatusCode> map = new HashMap<String, PipelineStepStatusCode>();
      map.put(ADD, PipelineStepStatusCode.DIVERT_PIPELINE);
      map.put(DELETE, PipelineStepStatusCode.CONTINUE);
      selector.setStatusCodeMap(map);
      final HashMap<String, List<PipelineStep>> opMap = new HashMap<String, List<PipelineStep>>();
      opMap.put(DELETE, new ArrayList<PipelineStep>());
      selector.setOperationMap(opMap);
      selector.prepare();
      final Document doc = new Document();
      doc.setOperation(ADD);
      try {
         selector.execute(doc);
         fail("Should throw exception!");
      } catch (PipelineException e) {
         // Ignoring
      }
      doc.setOperation(DELETE);
      final PipelineStepStatus status = selector.execute(doc);
      assertEquals(status.getStatusCode(), PipelineStepStatusCode.CONTINUE);
   }
   
   public void testExecute() throws Exception {
      final PipelineSelector selector = new PipelineSelector();
      selector.setName("selector");
      final HashMap<String, PipelineStepStatusCode> codeMap = new HashMap<String, PipelineStepStatusCode>();
      codeMap.put(ADD, PipelineStepStatusCode.DIVERT_PIPELINE);
      selector.setStatusCodeMap(codeMap);
      final HashMap<String, List<PipelineStep>> opMap = new HashMap<String, List<PipelineStep>>();
      opMap.put(ADD, new ArrayList<PipelineStep>());
      selector.setOperationMap(opMap);
      selector.prepare();
      final Document doc = new Document();
      doc.setOperation(ADD);
      PipelineStepStatus status = selector.execute(doc);
      assertEquals(status.getStatusCode(), PipelineStepStatusCode.DIVERT_PIPELINE);
      assertNotNull(status.getSubPipeline());
      doc.setOperation(DELETE);
      status = selector.execute(doc);
      assertEquals(status.getStatusCode(), PipelineStepStatusCode.CONTINUE);
      assertNull(status.getSubPipeline());
   }
}
