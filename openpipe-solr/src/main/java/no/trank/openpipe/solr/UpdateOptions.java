package no.trank.openpipe.solr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;

/**
 * @version $Revision$
 */
public class UpdateOptions {
   // Add options
   private boolean allowDups;
   private boolean overwritePending = !allowDups;
   private boolean overwriteCommitted = !allowDups;
   // Commit/Optimize options
   private boolean waitFlush = true;
   private boolean waitSearcher = true;
   // Delete options
   private boolean fromPending = true;
   private boolean fromCommitted = true;

   public boolean isAllowDups() {
      return allowDups;
   }

   public void setAllowDups(boolean allowDups) {
      this.allowDups = allowDups;
   }

   public boolean isOverwritePending() {
      return overwritePending;
   }

   public void setOverwritePending(boolean overwritePending) {
      this.overwritePending = overwritePending;
   }

   public boolean isOverwriteCommitted() {
      return overwriteCommitted;
   }

   public void setOverwriteCommitted(boolean overwriteCommitted) {
      this.overwriteCommitted = overwriteCommitted;
   }

   public boolean isWaitFlush() {
      return waitFlush;
   }

   public void setWaitFlush(boolean waitFlush) {
      this.waitFlush = waitFlush;
   }

   public boolean isWaitSearcher() {
      return waitSearcher;
   }

   public void setWaitSearcher(boolean waitSearcher) {
      this.waitSearcher = waitSearcher;
   }

   public boolean isFromPending() {
      return fromPending;
   }

   public void setFromPending(boolean fromPending) {
      this.fromPending = fromPending;
   }

   public boolean isFromCommitted() {
      return fromCommitted;
   }

   public void setFromCommitted(boolean fromCommitted) {
      this.fromCommitted = fromCommitted;
   }

   public Iterator<Attribute> createAddAttributes(XMLEventFactory factory) {
      // From Solr WIKI:
      // The defaults for overwritePending and overwriteCommitted are linked to allowDups such that those defaults make more sense
      //   * If allowDups is false (overwrite any duplicates), it implies that overwritePending and overwriteCommitted are true by default.
      //   * If allowDups is true (allow addition of duplicates), it implies that overwritePending and overwriteCommitted are false by default.
      final List<Attribute> list = new ArrayList<Attribute>(3);
      if (allowDups) {
         list.add(factory.createAttribute("allowDups", Boolean.toString(allowDups)));
      }
      if (overwritePending == allowDups) {
         list.add(factory.createAttribute("overwritePending", Boolean.toString(overwritePending)));
      }
      if (overwriteCommitted == allowDups) {
         list.add(factory.createAttribute("overwriteCommitted", Boolean.toString(overwriteCommitted)));
      }
      return list.iterator();
   }

   public Iterator<Attribute> createCOAttributes(XMLEventFactory factory) {
      final List<Attribute> list = new ArrayList<Attribute>(2);
      if (!waitFlush) {
         list.add(factory.createAttribute("waitFlush", Boolean.toString(waitFlush)));
      }
      if (!waitSearcher) {
         list.add(factory.createAttribute("waitSearcher", Boolean.toString(waitSearcher)));
      }
      return list.iterator();
   }

   public Iterator<Attribute> createDelAttributes(XMLEventFactory factory) {
      final List<Attribute> list = new ArrayList<Attribute>(2);
      if (!fromPending) {
         list.add(factory.createAttribute("fromPending", Boolean.toString(fromPending)));
      }
      if (!fromCommitted) {
         list.add(factory.createAttribute("fromCommitted", Boolean.toString(fromCommitted)));
      }
      return list.iterator();
   }
}
