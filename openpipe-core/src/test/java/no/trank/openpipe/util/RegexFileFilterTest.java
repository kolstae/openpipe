/*
 * Copyright 2008 T-Rank AS
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
package no.trank.openpipe.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * @version $Revision$
 */
public class RegexFileFilterTest extends TestCase {
   private static final Logger log = LoggerFactory.getLogger(RegexFileFilterTest.class);

   public void testAccept() {
      File gzFile = null, txtFile = null;
      try {
         gzFile = File.createTempFile("regextst", ".gz");
         txtFile = File.createTempFile("regextst", ".txt");

         RegexFileFilter regexFileFilter = new RegexFileFilter(".+\\.gz");
         assertTrue(regexFileFilter.accept(gzFile));
         assertFalse(regexFileFilter.accept(txtFile));
      } catch (IOException e) {
         fail("Test could not be completed, need tmp fileSystem access: " + e.getMessage());
      } finally {
         if (gzFile != null) {
            try {
               gzFile.delete();
            } catch (Exception e) {
               // Do nothing
            }
         }
         if (txtFile != null) {
            try {
               txtFile.delete();
            } catch (Exception e) {
               // Do nothing
            }
         }
      }

   }
}
