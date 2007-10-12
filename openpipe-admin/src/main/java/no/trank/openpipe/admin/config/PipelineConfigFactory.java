package no.trank.openpipe.admin.config;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version $Revision: 874 $
 */
public class PipelineConfigFactory {
//   private static final Logger log = LoggerFactory.getLogger(PipelineConfigFactory.class);
   
   @SuppressWarnings("unchecked")
   public List<String> getPipelineSteps() { 
      try {
         ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("pipelineConfigContext.xml");
         List<String> ret = (List<String>)context.getBean("steps");
         return ret;
      } catch (BeansException e) {
         e.printStackTrace();
//         log.error("Spring error", e);
         return null;
      }
   }
   
   public static void main(String[] args) {
      System.out.println(new PipelineConfigFactory().getPipelineSteps());
   }
}
