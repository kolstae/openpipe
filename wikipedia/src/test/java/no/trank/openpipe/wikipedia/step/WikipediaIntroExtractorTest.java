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
package no.trank.openpipe.wikipedia.step;/**
 *
 * @version $Revision$
 */

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

public class WikipediaIntroExtractorTest extends TestCase {
   WikipediaIntroExtractor wikipediaIntroExtractor;

   @Override
   protected void setUp() throws Exception {
      wikipediaIntroExtractor = new WikipediaIntroExtractor();

   }

   public void testWikipediaIntroExtractor() throws Exception {
      wikipediaIntroExtractor.setBodyField("text");
      wikipediaIntroExtractor.setIntroField("intro");
      wikipediaIntroExtractor.setIntroSize(160);
      wikipediaIntroExtractor.prepare();
      Document doc = new Document();
      doc.setFieldValue("text", getTestString());
      wikipediaIntroExtractor.execute(doc);
      assertNotNull(doc.getFieldValue("intro"));
      assertEquals("Akershus (fra norrønt akr, åker, og hús, borg eller kastell) er et norsk fylke, som grenser mot Hedmark, Oppland, Buskerud, Oslo og Østfold. Det består av to...", doc.getFieldValue("intro"));

      wikipediaIntroExtractor.finish(true);
      wikipediaIntroExtractor.setRelativeSizeField("url");
      wikipediaIntroExtractor.prepare();
      doc.removeField("intro");
      doc.setFieldValue("url", "http://test.trank.no/wiki/Habla");
      wikipediaIntroExtractor.execute(doc);
      assertNotNull(doc.getFieldValue("intro"));
      assertEquals("Akershus (fra norrønt akr, åker, og hús, borg eller kastell) er et norsk fylke, som grenser mot Hedmark, Oppland, Buskerud,...", doc.getFieldValue("intro"));

      doc.removeField("intro");
      doc.setFieldValue("text", "#REDIRECT [[Det norske Arbeiderparti]]");
      wikipediaIntroExtractor.execute(doc);
      assertNull(doc.getFieldValue("intro"));
   }

   public void testBug2WikipediaIntroExtractor() throws Exception {
      wikipediaIntroExtractor.setBodyField("text");
      wikipediaIntroExtractor.setIntroField("intro");
      wikipediaIntroExtractor.setIntroSize(160);
      wikipediaIntroExtractor.prepare();
      Document doc = new Document();
      doc.setFieldValue("text", getTest2String());
      wikipediaIntroExtractor.execute(doc);
      assertNotNull(doc.getFieldValue("intro"));
      assertEquals("Brann kan vise til:\n" +
            "* Brann (katastrofe)\n" +
            "* Ild\n" +
            "* Sportsklubben Brann\n" +
            "* Brand - Skuespill av Henrik Ibsen\n" +
            "en:Brann", doc.getFieldValue("intro"));
   }

   public void testBug3WikipediaIntroExtractor() throws Exception {
      wikipediaIntroExtractor.setBodyField("text");
      wikipediaIntroExtractor.setIntroField("intro");
      wikipediaIntroExtractor.setIntroSize(160);
      wikipediaIntroExtractor.prepare();
      Document doc = new Document();
      doc.setFieldValue("text", getTest3String());
      wikipediaIntroExtractor.execute(doc);
      assertNotNull(doc.getFieldValue("intro"));
      assertEquals("Biler kan refere til\n" +
            "* Biler er flertallsformen for bil.\n" +
            "* Biler (Cars) er en amerikansk animasjonsfilm.\n" +
            "* Biler", doc.getFieldValue("intro"));
   }

   public void testBug4WikipediaIntroExtractor() throws Exception {
      wikipediaIntroExtractor.setBodyField("text");
      wikipediaIntroExtractor.setIntroField("intro");
      wikipediaIntroExtractor.setIntroSize(160);
      wikipediaIntroExtractor.prepare();
      Document doc = new Document();
      doc.setFieldValue("text", getTest4String());
      wikipediaIntroExtractor.execute(doc);
      assertNotNull(doc.getFieldValue("intro"));

   }


   private String getTest4String() {
      return "{{MediaWiki:Groups-existing}}:";
   }

   private String getTest3String() {
      return ";Biler kan refere til\n" +
            "* Biler er flertallsformen for [[bil]].\n" +
            "* [[Biler (film)|Biler]] (''Cars'') er en amerikansk animasjonsfilm.\n" +
            "* [[Biler]]\n" +
            "{{Peker}}";
   }

   private String getTest2String() {
      return "'''Brann''' kan vise til:\n" +
            "\n" +
            "* [[Brann (katastrofe)]]\n" +
            "* [[Ild]]\n" +
            "* [[Sportsklubben Brann]]\n" +
            "* [[Brand]] - [[Skuespill]] av [[Henrik Ibsen]]\n" +
            "\n" +
            "{{peker}}\n" +
            "\n" +
            "[[en:Brann]]";
   }

   private String getTestString() {
      return ":''Se også [[Akershus festning]]''\n" +
            "{{Infoboks_fylke|\n" +
            "våpen={{PAGENAME}}_vapen.svg|\n" +
            "administrasjonssenter=[[Oslo]]|\n" +
            "areal=4 918|\n" +
            "befolkning=509&amp;nbsp;177|\n" +
            "befolkningsår=[[2007]]|\n" +
            "url=www.akershus.no/|\n" +
            "fylkesordførernavn=[[Nils Aage Jegstad]]|\n" +
            "fylkesordførerår=[[2007]]|\n" +
            "fylkesordførerparti=[[Høyre]]|\n" +
            "fylkesmannnavn= [[Hans J. Røsjorde]] &lt;br&gt;(felles for Oslo og Akershus)|\n" +
            "annet=}}\n" +
            "'''Akershus''' (fra norrønt ''akr'', åker, og ''hús'', borg eller kastell) er et [[Norge|norsk]] fylke, som grenser mot [[Hedmark]], [[Oppland]], [[Buskerud]], [[Oslo]] og [[Østfold]]. Det består av to adskilte deler. Kommunene Asker og Bærum utgjør en [[eksklave]] mellom Oslo og Buskerud. Etter Oslo er Akershus Norges nest største fylke etter innbyggertall, med vel en halv million innbyggere.\n" +
            "\n" +
            "[[Akershus hovedlen]] ble opprettet på 1500-tallet og omfattet foruten dagens Akershus også det nåværende Hedmark, Oppland, Buskerud og Oslo, foruten Askim, Eidsberg og Trøgstad i Østfold. I [[1662]] ble [[Akershus stiftamt]] opprettet, og [[1685]] ble [[Buskeruds amt|Buskerud]] skilt ut som eget amt. I [[1768]] ble også Hedmark og Oppland skilt ut til [[Oplandenes amt]], og i [[1842]] ble [[Christiania]] (Oslo) utskilt fra Akershus. [[1. januar]] [[1919]] bortfalt betegnelsen amt til fordel for betegnelsen fylke.\n" +
            "\n" +
            "Akershus er sentrert rundt det urbaniserte Oslo-området, og er det eneste norske fylke som har sitt administrasjonssenter i et annet fylke (i Oslo).";
   }
}