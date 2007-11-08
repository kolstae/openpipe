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
package no.trank.openpipe.solr.util;

import java.util.Map;

import org.apache.solr.analysis.TokenFilterFactory;

/**
 * @version $Revision$
 */
public class TokenFilterFactoryFactory {
   
   public static TokenFilterFactory createFactory(String className, Map<String, String> args) {
      try {
         final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
         if (TokenFilterFactory.class.isAssignableFrom(clazz)) {
            final Object obj = clazz.newInstance();
            if (obj instanceof TokenFilterFactory) {
               final TokenFilterFactory tff = (TokenFilterFactory) obj;
               tff.init(args);
               return tff;
            }
         }
         throw new IllegalArgumentException("Class " + className + " does not implement TokenFilterFactory");
      } catch (ClassNotFoundException e) {
         throw new IllegalArgumentException(e);
      } catch (IllegalAccessException e) {
         throw new IllegalArgumentException(e);
      } catch (InstantiationException e) {
         throw new IllegalArgumentException(e);
      }
   }
   
   private TokenFilterFactoryFactory() {
   }
}
