package no.trank.openpipe.reader;

import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DomRawData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.xml.namespace.QName;

import static org.junit.Assert.*;

/**
 * @author David Smiley - dsmiley@mitre.org
 */
public class MultiXmlDocumentReaderTest {

   MultiXmlDocumentReader reader;

   @Before
   public void setUp() {
      reader = new MultiXmlDocumentReader();
   }

   @After
   public void tearDown() {
      reader.close();
   }

   @Test
   public void testIterator() throws Exception {
      reader.setElemMatch(QName.valueOf("{mynamespace}state"));
      reader.setInput(new ClassPathResource("/dummyXml.xml"));

      int count = 0;
      for (Document document : reader) {
         count++;
         assertNotNull(((DomRawData)document.getRawData()).getDom());
      }
      assertEquals(2,count);
   }

   @Test
   public void testNotFound() throws Exception {
      reader.setElemMatch(QName.valueOf("{mynamespace}notfound"));
      reader.setInput(new ClassPathResource("/dummyXml.xml"));

      for (Document document : reader) {
         fail();
      }
   }
}
