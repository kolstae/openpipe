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
package no.trank.openpipe.step;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class ChopFieldTest extends TestCase {
   private ChopField chopField;
   @Override
   protected void setUp() throws Exception {
      chopField = new ChopField();
      chopField.setInputField("in");
      chopField.prepare();
   }

   @Override
   protected void tearDown() throws Exception {
      chopField.finish(true);
   }

   public void testBasicChop() throws Exception {
      Document doc = new Document();
      doc.setFieldValue("in", "Kaffe er en drikk laget av frukten fra kaffeplanten (Arabica eller Coffea arabica og Robusta eller Coffea canephora utgjør ca. 99 %). Frukten blir skrellet for skall og fruktkjøtt, og kjernen (steinen), det vi kaller bønnene, blir tørket, brent og malt opp. Finheten på kaffepulveret varierer etter hvordan kaffen skal tilberedes. Kaffen blandes på forskjellige måter med vann, og drikkes i de fleste tilfeller varm.");
      chopField.setChopLength(160);
      chopField.execute(doc);
      assertNotNull(doc.getFieldValue("in"));
      assertEquals("Kaffe er en drikk laget av frukten fra kaffeplanten (Arabica eller Coffea arabica og Robusta eller Coffea canephora utgjør ca. 99 %). Frukten blir skrellet", doc.getFieldValue("in"));
   }

   public void testChopWithAppend() throws Exception {
      Document doc = new Document();
      doc.setFieldValue("in", "Kaffe er en drikk laget av frukten fra kaffeplanten (Arabica eller Coffea arabica og Robusta eller Coffea canephora utgjør ca. 99 %). Frukten blir skrellet for skall og fruktkjøtt, og kjernen (steinen), det vi kaller bønnene, blir tørket, brent og malt opp. Finheten på kaffepulveret varierer etter hvordan kaffen skal tilberedes. Kaffen blandes på forskjellige måter med vann, og drikkes i de fleste tilfeller varm.");
      chopField.setChopLength(160);
      chopField.setAppendOnChop("...");
      chopField.execute(doc);
      assertNotNull(doc.getFieldValue("in"));
      assertEquals("Kaffe er en drikk laget av frukten fra kaffeplanten (Arabica eller Coffea arabica og Robusta eller Coffea canephora utgjør ca. 99 %). Frukten blir skrellet...", doc.getFieldValue("in"));

      doc.setFieldValue("in", "Kaffe er en drikk laget av frukten fra kaffeplanten.");
      chopField.execute(doc);
      assertNotNull(doc.getFieldValue("in"));
      assertEquals("Kaffe er en drikk laget av frukten fra kaffeplanten.", doc.getFieldValue("in"));

   }
}
