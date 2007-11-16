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
package no.trank.openpipe.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;
import no.trank.openpipe.config.annotation.NullNotEmpty;

/**
 * @version $Revision$
 */
public class BeanValidatorTest extends TestCase {

   public void testValidateNotNull() throws PipelineException {
      try {
         BeanValidator.validate(new BeanNotNull(null));
         fail("no exception thrown on @NotNull");
      } catch (PipelineException e) {
         // Ignoring
      }
      BeanValidator.validate(new BeanNotNull(""));
      BeanValidator.validate(new BeanNotNull("Some text"));
   }

   public void testValidateNotEmpty() throws PipelineException {
      try {
         BeanValidator.validate(new BeanNotEmpty(null));
         fail("no exception thrown on @NotEmpty");
      } catch (PipelineException e) {
         // Ignoring
      }
      try {
         BeanValidator.validate(new BeanNotEmpty(""));
         fail("no exception thrown on @NotEmpty");
      } catch (PipelineException e) {
         // Ignoring
      }
      BeanValidator.validate(new BeanNotEmpty("Some text"));
   }

   public void testValidateNullNotEmpty() throws PipelineException {
      BeanValidator.validate(new BeanNullNotEmpty(null));
      try {
         BeanValidator.validate(new BeanNullNotEmpty(""));
         fail("no exception thrown on @NullNotEmpty");
      } catch (PipelineException e) {
         // Ignoring
      }
      BeanValidator.validate(new BeanNullNotEmpty("Some text"));
   }

   public void testValidateInherited() throws PipelineException {
      final List<String> notEmpty = Arrays.asList("test");
      final List<String> empty = Collections.emptyList();
      BeanValidator.validate(new InheritNotNull(null, notEmpty, empty));
      BeanValidator.validate(new InheritNotNull(null, notEmpty, notEmpty));
      BeanValidator.validate(new InheritNotNull(notEmpty, notEmpty, notEmpty));
      BeanValidator.validate(new InheritNotNull(notEmpty, notEmpty, empty));
      // Test fail on @NullNotEmpty
      try {
         BeanValidator.validate(new InheritNotNull(empty, notEmpty, empty));
         fail("no exception thrown on @NullNotEmpty");
      } catch (PipelineException e) {
         // Ignoring
      }
      // Test fail on @NotEmpty
      try {
         BeanValidator.validate(new InheritNotNull(null, null, empty));
         fail("no exception thrown on @NotEmpty");
      } catch (PipelineException e) {
         // Ignoring
      }
      try {
         BeanValidator.validate(new InheritNotNull(notEmpty, empty, empty));
         fail("no exception thrown on @NotEmpty");
      } catch (PipelineException e) {
         // Ignoring
      }
      // Test fail on @NotNull
      try {
         BeanValidator.validate(new InheritNotNull(null, notEmpty, null));
         fail("no exception thrown on @NotNull");
      } catch (PipelineException e) {
         // Ignoring
      }
   }

   private static class BeanNotNull {
      @NotNull
      private String f1;

      private BeanNotNull(String f1) {
         this.f1 = f1;
      }

      public String getF1() {
         return f1;
      }
   }

   private static class BeanNotEmpty {
      @NotEmpty
      private String f1;

      private BeanNotEmpty(String f1) {
         this.f1 = f1;
      }

      public String getF1() {
         return f1;
      }
   }

   private static class BeanNullNotEmpty {
      @NullNotEmpty
      private String f1;

      private BeanNullNotEmpty(String f1) {
         this.f1 = f1;
      }

      public String getF1() {
         return f1;
      }
   }

   private static class SuperNullNotEmpty {
      @NullNotEmpty
      private List<String> nullNotEmpty;

      private SuperNullNotEmpty(List<String> nullNotEmpty) {
         this.nullNotEmpty = nullNotEmpty;
      }

      public List<String> getNullNotEmpty() {
         return nullNotEmpty;
      }
   }

   private static class SuperNotEmpty extends SuperNullNotEmpty {
      @NotEmpty
      private List<String> notEmpty;

      private SuperNotEmpty(List<String> nullNotEmpty, List<String> notEmpty) {
         super(nullNotEmpty);
         this.notEmpty = notEmpty;
      }

      public List<String> getNotEmpty() {
         return notEmpty;
      }
   }

   private static class InheritNotNull extends SuperNotEmpty {
      @NotNull
      private List<String> notNull;

      private InheritNotNull(List<String> nullNotEmpty, List<String> notEmpty, List<String> notNull) {
         super(nullNotEmpty, notEmpty);
         this.notNull = notNull;
      }

      public List<String> getNotNull() {
         return notNull;
      }
   }
}
