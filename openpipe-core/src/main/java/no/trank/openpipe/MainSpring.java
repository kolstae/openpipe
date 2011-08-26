package no.trank.openpipe;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author David Smiley - dsmiley@mitre.org
 */
public class MainSpring {
   private static final Logger log = LoggerFactory.getLogger(MainSpring.class);

   //TODO install top level thread logging handler?

   public static void main(String[] args) {

      String path = "pipeline-spring.xml";
      if (args.length > 1)
         throw new IllegalArgumentException("Expecting 1 arg or none.");
      if (args.length == 1) {
         path = args[0];
      }
      FileSystemXmlApplicationContext appContext = new FileSystemXmlApplicationContext(path);
      try {
         Runnable pipelineApplication = appContext.getBean("pipelineApplicationBean", Runnable.class);
         pipelineApplication.run();
      } catch (Throwable t) {
         log.error(t.toString(),t);
         throw Throwables.propagate(t);
      } finally {
         appContext.close();
      }
   }
}
