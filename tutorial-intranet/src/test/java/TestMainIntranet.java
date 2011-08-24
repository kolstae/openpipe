import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author David Smiley - dsmiley@mitre.org
 */
public class TestMainIntranet {

   @Test
   public void test() {
      //validates xml
      ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("intranetApplicationContext.xml");
      Runnable pipelineApplication = appContext.getBean("pipelineApplicationBean", Runnable.class);

   }
}
