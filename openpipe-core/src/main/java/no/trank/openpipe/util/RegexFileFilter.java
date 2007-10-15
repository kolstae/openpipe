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
