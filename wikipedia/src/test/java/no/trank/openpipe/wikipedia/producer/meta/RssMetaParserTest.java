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
package no.trank.openpipe.wikipedia.producer.meta;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class RssMetaParserTest extends TestCase {

   public void testParseRssAndFindMd5() throws Exception {
      final String rss = loadResource("/enwiki-latest-pages-articles.xml.bz2-rss.xml");
      final RssMetaParser parser = new RssMetaParser(rss);
      assertEquals("http://download.wikimedia.org/enwiki/20080724/enwiki-20080724-pages-articles.xml.bz2",
              parser.getFileUrl().toExternalForm());
      assertEquals("http://download.wikimedia.org/enwiki/20080724/enwiki-20080724-md5sums.txt",
              parser.getMd5Url().toExternalForm());
      final String md5sums = loadResource("/enwiki-20080724-md5sums.txt");
      assertEquals("30c9b48de3ede527289bcdb810126723", parser.findMd5(md5sums));
   }

   private String loadResource(String name) throws IOException {
      final InputStream in = getClass().getResourceAsStream(name);
      assertNotNull("Missing test-resource: '" + name + '\'', in);
      final StringWriter writer = new StringWriter(128);
      final InputStreamReader reader = new InputStreamReader(in);
      try {
         final char[] buf = new char[1024 * 4];
         int read;
         while ((read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
         }
      } finally {
         reader.close();
      }
      return writer.toString();
   }
}
