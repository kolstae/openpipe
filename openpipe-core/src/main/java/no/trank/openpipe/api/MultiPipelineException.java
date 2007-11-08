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
package no.trank.openpipe.api;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @version $Revision$
 */
public class MultiPipelineException extends PipelineException {
   private static final long serialVersionUID = 8297986555121255948L;
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
