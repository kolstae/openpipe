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
package no.trank.openpipe.lemmatizer.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @version $Revision$
 */
public class LemmatizeModelFactory {

   public static LemmatizeModel loadModelFromFile(String fileName) throws IOException {
      final InputStream in;
      if (fileName.endsWith(".gz")) {
         in = new GZIPInputStream(new FileInputStream(fileName));
      } else {
         in = new FileInputStream(fileName);
      }
      return loadModel(in);
   }

   public static LemmatizeModel loadModel(InputStream in) throws IOException {
      try {
         final LemmatizeModel model = new LemmatizeModel();
         model.read(in);
         return model;
      } finally {
         try {
            in.close();
         } catch (IOException e) {
            // Ignoring
         }
      }
   }

   private LemmatizeModelFactory() {
   }
}
