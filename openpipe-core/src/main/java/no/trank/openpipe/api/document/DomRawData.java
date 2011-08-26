package no.trank.openpipe.api.document;

import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps another RawData and provides access to a DOM {@link Node}.
 * @author David Smiley - dsmiley@mitre.org
 */
public class DomRawData implements RawData {
   private RawData delegate;

   private Node dom;

   public DomRawData(RawData delegate, Node dom) {
      this.delegate = delegate;
      this.dom = dom;
   }

   public Node getDom() {
      return dom;
   }

   @Override
   public InputStream getInputStream() throws IOException {
      if (delegate != null)
         return delegate.getInputStream();
      throw new UnsupportedOperationException("dom tostring()");
   }

   @Override
   public int getLength() {
      if (delegate != null)
         return delegate.getLength();
      return -1;
   }

   @Override
   public void release() {
      if (delegate != null)
         delegate.release();
      dom = null;
   }

   @Override
   public boolean isReleased() {
      return delegate != null ? delegate.isReleased() : dom == null;
   }
}
