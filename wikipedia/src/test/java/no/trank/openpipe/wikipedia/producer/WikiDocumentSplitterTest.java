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
package no.trank.openpipe.wikipedia.producer;/**
 *
 * @version $Revision$
 */

import junit.framework.*;

import no.trank.openpipe.wikipedia.producer.WikiDocumentSplitter;

public class WikiDocumentSplitterTest extends TestCase {
   WikiDocumentSplitter wikiDocumentSplitter;

   public void testWikiDocumentSplitter() throws Exception {
      try {
         wikiDocumentSplitter = new WikiDocumentSplitter(WikiDocumentSplitterTest.class.getClassLoader().getResourceAsStream("noexists"));
         fail("File does not exist, illegal argument exception should be thrown.");
      } catch (IllegalArgumentException e) {
         // Everything is fine.
      }
      wikiDocumentSplitter = new WikiDocumentSplitter(WikiDocumentSplitterTest.class.getClassLoader().getResourceAsStream("testwiki.xml"));
      assertTrue(wikiDocumentSplitter.hasNext());
      final String s = wikiDocumentSplitter.next();
      assertTrue("Element is " + s + ", should start with <page>", s.startsWith("<page>"));
      assertTrue("Element is " + s + ", should contain: <text xml:space=\"preserve\">#REDIRECT [[Det norske Arbeiderparti]]</text>", s.contains("<text xml:space=\"preserve\">#REDIRECT [[Det norske Arbeiderparti]]</text>"));
      assertTrue(wikiDocumentSplitter.hasNext());
      assertTrue(wikiDocumentSplitter.next().startsWith("<page>"));
      assertFalse(wikiDocumentSplitter.hasNext());
   }
}