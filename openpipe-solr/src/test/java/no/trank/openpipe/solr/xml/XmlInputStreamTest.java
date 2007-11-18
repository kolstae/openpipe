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
package no.trank.openpipe.solr.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class XmlInputStreamTest extends TestCase {
   public void testRead() throws Exception {
      String testString = "<xml tralala>";
      InputStream is = new XmlInputStream(new ByteArrayInputStream(("   " + testString).getBytes()));
      
      for(int i = 0; i < testString.length(); ++i) {
         assertEquals(is.read(), testString.charAt(i));
      }
      
      assertEquals(is.read(), -1);
   }

   public void testReadBuffer() throws Exception {
      String testString = "<xml tralala>";
      InputStream is = new XmlInputStream(new ByteArrayInputStream(("   " + testString).getBytes()));
      
      byte[] b = new byte[4096];
      assertEquals(testString.length(), is.read(b, 0, b.length));
      
      for(int i = 0; i < testString.length(); ++i) {
         assertEquals(testString.charAt(i), b[i]);
      }
   }
   
   public void testSkip() throws Exception {
      String testString = "<xml tralala>";
      InputStream is = new XmlInputStream(new ByteArrayInputStream(("   " + testString).getBytes()));
      
      is.skip(1);
      byte[] b = new byte[4096];
      assertEquals(testString.length()-1, is.read(b, 0, b.length));
      
      for(int i = 1; i < testString.length(); ++i) {
         assertEquals(testString.charAt(i), b[i-1]);
      }
   }
}
