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
package no.trank.openpipe.wikipedia.step;

import junit.framework.TestCase;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class WikipediaUrlBuilderTest extends TestCase {
   private WikipediaUrlBuilder urlBuilder;

   public void testSlashNotEncoded() throws PipelineException {
      final Document doc = new Document();
      doc.setFieldValue("title", "Portal:Golf/Ukens:artikkel");
      urlBuilder.execute(doc);
      assertEquals("Portal%3AGolf/Ukens%3Aartikkel", doc.getFieldValue("url"));
      doc.setFieldValue("title", "//Portal:Golf/Ukens:artikkel");
      urlBuilder.execute(doc);
      assertEquals("//Portal%3AGolf/Ukens%3Aartikkel", doc.getFieldValue("url"));
   }

   public void testSpaceReplacedWithUnderscore() throws PipelineException {
      final Document doc = new Document();
      doc.setFieldValue("title", "Portal Golf Ukens artikkel æøå");
      urlBuilder.execute(doc);
      assertEquals("Portal_Golf_Ukens_artikkel_%C3%A6%C3%B8%C3%A5", doc.getFieldValue("url"));
   }

   public void testExecuteDoesNotFailWhenTitleIsNull() throws PipelineException {
      urlBuilder.execute(new Document());
      assertNull(new Document().getFieldValue("url"));
   }

   @Override
   protected void setUp() throws Exception {
      urlBuilder = new WikipediaUrlBuilder();
      urlBuilder.setBaseUrl("");
      urlBuilder.prepare();
   }
}
