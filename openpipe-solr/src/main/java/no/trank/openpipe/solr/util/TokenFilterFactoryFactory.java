package no.trank.openpipe.solr.util;

import java.util.Map;

import org.apache.solr.analysis.TokenFilterFactory;

/**
 * @version $Revision$
 */
public class TokenFilterFactoryFactory {
   
   public static TokenFilterFactory createFactory(String className, Map<String, String> args) {
      try {
         final Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
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
