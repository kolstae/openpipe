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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;
import no.trank.openpipe.config.annotation.NullNotEmpty;

/**
 * A utility class for validating fields of an instance according to the annotations: {@link NotNull}, {@link NotEmpty}
 * and {@link NullNotEmpty}.
 *
 * @version $Revision$
 */
public class BeanValidator {
   private static final Logger log = LoggerFactory.getLogger(BeanValidator.class);

   /**
    * Validates fields of the given object, including fields of super classes.
    * <br>
    * Currently {@link NotNull}, {@link NotEmpty} and {@link NullNotEmpty} are supported. {@link NotEmpty} and
    * {@link NullNotEmpty} are supported for fields of type {@link CharSequence}, {@link Collection} and
    * {@link Map}. For unsupported types a warning is logged, but no exception is thrown.
    *
    * @param obj the object whose fields should be validated.
    *
    * @throws PipelineException if {@link NotNull} is declared for a field that is <tt>null</tt>, or if {@link NotEmpty}
    * is declared for a field that is <tt>empty</tt> or <tt>null</tt>.
    */
   public static void validate(Object obj) throws PipelineException {
      Class<?> clazz = obj.getClass();
      while (clazz != null && clazz != Object.class) {
         validate(obj, clazz);
         clazz = clazz.getSuperclass();
      }
   }

   private static void validate(Object obj, Class<?> clazz) throws PipelineException {
      final Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
         final Restriction restriction = getRestriction(field);
         if (restriction != Restriction.NONE) {
            final Object o = getObject(obj, field);
            if (o != null) {
               if (restriction.notEmpty() && empty(o)) {
                  throw new PipelineException("Field " + field.getName() + " cannot be empty");
               }
            } else if (restriction.notNull()) {
               throw new PipelineException("Field " + field.getName() + " cannot be null");
            }
         }
      }
   }

   private static boolean empty(Object obj) {
      if (obj instanceof CharSequence) {
         return ((CharSequence) obj).length() <= 0;
      }
      if (obj instanceof Collection) {
         return ((Collection<?>) obj).isEmpty();
      }
      if (obj instanceof Map) {
         return ((Map<?, ?>) obj).isEmpty();
      }
      log.warn("Empty check not supported for type: {}", obj.getClass().getName());
      return false;
   }

   private static Object getObject(Object obj, Field field) {
      field.setAccessible(true);
      try {
         return field.get(obj);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   private static Restriction getRestriction(AccessibleObject object) {
      if (declaresNotEmpty(object)) {
         return Restriction.NOT_EMPTY;
      }
      if (declaresNullNotEmpty(object)) {
         return Restriction.NULL_NOT_EMPTY;
      }
      return declaresNotNull(object) ? Restriction.NOT_NULL : Restriction.NONE;
   }

   private static boolean declaresNotNull(AccessibleObject field) {
      return field.getAnnotation(NotNull.class) != null;
   }

   private static boolean declaresNotEmpty(AccessibleObject field) {
      return field.getAnnotation(NotEmpty.class) != null;
   }

   private static boolean declaresNullNotEmpty(AccessibleObject field) {
      return field.getAnnotation(NullNotEmpty.class) != null;
   }

   private static enum Restriction {
      NONE(false, false), NOT_NULL(true, false), NOT_EMPTY(true, true), NULL_NOT_EMPTY(false, true);
      private final boolean notNull;
      private final boolean notEmpty;

      Restriction(boolean notNull, boolean notEmpty) {
         this.notNull = notNull;
         this.notEmpty = notEmpty;
      }

      public boolean notNull() {
         return notNull;
      }

      public boolean notEmpty() {
         return notEmpty;
      }
   }
}
