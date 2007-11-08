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
package no.trank.openpipe.solr.producer.xml;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLInputFactory;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.solr.producer.xml.XmlStreamDocumentReader;

/**
 * @version $Revision$
 */
public class XmlStreamDocumentReaderTest extends TestCase {
   
   public void testInvalidXML() throws Exception {
      final XmlStreamDocumentReader reader = setUpDocumentReader("/xml/invalid.xml");
      final Iterator<Document> iterator = reader.iterator();
      assertTrue(iterator.hasNext());
      iterator.next();
      assertFalse(iterator.hasNext());
      try {
         iterator.next();
         fail("next() does not throw exception");
      } catch (NoSuchElementException e) {
         // Should throw exception
      }
   }
   
   public void testAddXML() throws Exception {
      final XmlStreamDocumentReader reader = setUpDocumentReader("/xml/add.xml");
      int count = 0;
      for (Document doc : reader) {
         assertEquals("add", doc.getOperation());
         final String docBoost = doc.getFieldValue("boost");
         assertTrue(count == 0 ? docBoost == null : docBoost != null);
         for (int i = 0; i < 4; i++) {
            assertEquals(i, doc.getFields("field" + i).size());
            if (i > 0) {
               assertEquals("value" + i, doc.getFieldValue("field" + i));
            }
         }
         final List<AnnotatedField> list = doc.getFields("boosted");
         assertEquals(1, list.size());
         assertTrue(list.get(0).iterator("boost").hasNext());
         count++;
      }
      assertEquals(2, count);
   }
   
   private XmlStreamDocumentReader setUpDocumentReader(String resource) throws Exception {
      final InputStream in = getClass().getResourceAsStream(resource);
      assertNotNull(in);
      return new XmlStreamDocumentReader(XMLInputFactory.newInstance().createXMLStreamReader(in));
   }
}
