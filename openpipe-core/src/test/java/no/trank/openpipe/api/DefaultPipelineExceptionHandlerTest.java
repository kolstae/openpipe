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