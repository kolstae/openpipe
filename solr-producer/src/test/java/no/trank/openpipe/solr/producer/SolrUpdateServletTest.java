package no.trank.openpipe.solr.producer;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import no.trank.openpipe.api.Pipeline;
import no.trank.openpipe.api.PipelineStep;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

/**
 * @version $Revision$
 */
public class SolrUpdateServletTest extends TestCase {
   private SolrUpdateServlet servlet;
   private IMocksControl control;
   private HttpServletRequest req;
   private HttpServletResponse resp;

   public void testDoPostAdd() throws Exception {
      setUpStatus(HttpServletResponse.SC_OK);
      setUpInputStream("/xml/add.xml");
      control.replay();
      servlet.doPost(req, resp);
      control.verify();
   }

   public void testDoPostFail() throws Exception {
      setUpStatus(HttpServletResponse.SC_BAD_REQUEST);
      setUpInputStream("/xml/invalid.xml");
      control.replay();
      servlet.doPost(req, resp);
      control.verify();
   }

   private void setUpInputStream(String resource) throws Exception {
      final InputStream in = getClass().getResourceAsStream(resource);
      assertNotNull(in);
      expect(req.getInputStream()).andReturn(new WrapperServletInputStream(in));
   }

   private void setUpStatus(int status) {
      resp.setStatus(status);
      expectLastCall().atLeastOnce();
   }

   @Override
   protected void setUp() throws Exception {
      servlet = new SolrUpdateServlet();
      control = EasyMock.createNiceControl();
      control.checkOrder(false);
      servlet.setPipeline(new Pipeline(Collections.<PipelineStep>emptyList()));
      req = control.createMock(HttpServletRequest.class);
      expect(req.getRequestURI()).andReturn("test.uri").anyTimes();
      resp = control.createMock(HttpServletResponse.class);
      expect(resp.getOutputStream()).andReturn(new NullOutputStream()).once();
   }

   private static class WrapperServletInputStream extends ServletInputStream {
      private final InputStream in;

      public WrapperServletInputStream(InputStream in) {
         this.in = in;
      }

      @Override
      public int read() throws IOException {
         return in.read();
      }
   }

   private static class NullOutputStream extends ServletOutputStream {
      @Override
      public void write(int b) throws IOException {
      }
   }
}
