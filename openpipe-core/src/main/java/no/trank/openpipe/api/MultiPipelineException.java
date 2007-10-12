package no.trank.openpipe.api;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @version $Revision: 874 $
 */
public class MultiPipelineException extends PipelineException {
   private List<Throwable> exceptions = Collections.emptyList();

   public MultiPipelineException() {
      this(null);
   }

   public MultiPipelineException(String pipelineStepName) {
      super(null, null, pipelineStepName);
   }

   public boolean add(Throwable throwable) {
      if (exceptions.isEmpty()) {
         exceptions = new ArrayList<Throwable>();
      }
      return exceptions.add(throwable);
   }

   public int size() {
      return exceptions.size();
   }

   public boolean isEmpty() {
      return exceptions.isEmpty();
   }

   @Override
   public void printStackTrace(PrintStream s) {
      super.printStackTrace(s);
      for (Throwable cause : exceptions) {
         cause.printStackTrace(s);
      }
   }

   @Override
   public void printStackTrace(PrintWriter s) {
      super.printStackTrace(s);
      for (Throwable cause : exceptions) {
         cause.printStackTrace(s);
      }
   }

   @Override
   public StackTraceElement[] getStackTrace() {
      return super.getStackTrace();
   }

   @Override
   public String toString() {
      return getClass().getName() + exceptions;
   }
}
