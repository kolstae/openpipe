package no.trank.openpipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version $Revision:670 $
 */
public class Main {
   private static final Logger log = LoggerFactory.getLogger(Main.class);
   
   public static void main(String[] args) {
      try {
         ClassPathXmlApplicationContext appContext;
         if (args.length > 0) {
            appContext = new ClassPathXmlApplicationContext(args[0]);
         } else {
            appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
         }
         try {
            Runnable pipelineApplication = (Runnable) appContext.getBean("pipelineApplicationBean", Runnable.class);
            pipelineApplication.run();
         } finally {
            appContext.close();
         }
      } catch (BeansException e) {
         log.error("Spring error", e);
      }
   }
}
