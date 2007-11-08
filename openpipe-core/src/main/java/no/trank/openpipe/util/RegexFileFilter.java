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
package no.trank.openpipe.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * @version $Revision$
 */
public class RegexFileFilter implements FileFilter {
   private Pattern[] fileNamePatterns;

   public RegexFileFilter(String fileNamePatterns) {
      this(new String[]{fileNamePatterns});
   }

   public RegexFileFilter(String[] fileNamePatterns) {
      this.fileNamePatterns = new Pattern[fileNamePatterns.length];
      for (int i = 0; i < fileNamePatterns.length; i++) {
         this.fileNamePatterns[i] = Pattern.compile(fileNamePatterns[i]);
         
      }
   }

   @Override
   public boolean accept(File file) {
      final String name = file.getName();
      for (Pattern pattern : fileNamePatterns) {
         if (pattern.matcher(name).matches()) {
            return true;
         }
      }
      return false;
   }
}
