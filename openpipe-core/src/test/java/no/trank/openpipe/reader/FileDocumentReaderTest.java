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
package no.trank.openpipe.reader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import junit.framework.TestCase;

import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class FileDocumentReaderTest extends TestCase {
   private static final List<String> FILE_NAMES = Arrays.asList("a.txt", "b.txt", "c.txt", "/a/a/a/aaa.txt", "/a/a/a/bbb.txt");
   private static final Set<String> PATHS = new HashSet<String>(); 
   
   public void testIterate() throws Exception {
      final File dir = File.createTempFile("fileDoc", "");
      try {
         dir.delete();
         dir.mkdir();
         for (String file : FILE_NAMES) {
            writeFile(dir, file);
         }
         final FileDocumentReader reader = new FileDocumentReader();
         reader.setDirectory(dir.getAbsolutePath());
         reader.init();
         int count = 0;
         for (Document doc : reader) {
            assertTrue(FILE_NAMES.contains(doc.getFieldValue("fileName")));
            count++;
         }
         assertEquals(3, count);
         reader.close();
         reader.setMaxDepth(-1);
         reader.init();
         count = 0;
         for (Document doc : reader) {
            assertTrue(PATHS.contains(doc.getFieldValue("pathName")));
            count++;
         }
         assertEquals(5, count);
      } finally {
         deleteDirs(dir);
      }
   }

   public void testIterateWithRegEx() throws Exception {
      final File dir = File.createTempFile("fileDoc", "");
      try {
         dir.delete();
         dir.mkdir();
         writeFile(dir, "testfile1.gz");
         writeFile(dir, "testfile2.txt");
         writeFile(dir, "testfile3.txt");
         writeFile(dir, "testfile4.gz");

         Set<String> legalFileNames = new HashSet<String>();
         legalFileNames.add("testfile1.gz");
         legalFileNames.add("testfile4.gz");

         FileDocumentReader reader = new FileDocumentReader();
         reader.setDirectory(dir.getAbsolutePath());
         reader.setRegexPattern(".+\\.gz");
         reader.init();

         int count = 0;
         for (Document document : reader) {
            count++;
            assertTrue(legalFileNames.contains(document.getFieldValue("fileName")));
         }
         assertEquals(2, count);
      } finally {
         deleteDirs(dir);
      }
   }

   private static void deleteDirs(File dir) {
      final File[] files = dir.listFiles();
      for (File file : files) {
         if (file.isDirectory()) {
            deleteDirs(file);
         } else {
            file.delete();
         }
      }
      dir.delete();
   }

   private static void writeFile(File dir, String fileName) throws IOException {
      final File file = new File(dir, fileName);
      file.getParentFile().mkdirs();
      final FileWriter writer = new FileWriter(file);
      try {
         writer.write(fileName);
         PATHS.add(file.getAbsolutePath());
      } finally {
         writer.close();
      }
   }
}
