package no.trank.openpipe.util;

import java.io.File;
import java.util.Comparator;

/**
 * @version $Revision: 874 $
 */
public class FilesFirstComparator implements Comparator<File> {

   public int compare(File f1, File f2) {
      if (f1.isDirectory()) {
         if (f2.isDirectory()) {
            return f1.compareTo(f2);
         }
         return -1;
      }
      if (f2.isDirectory()) {
         return 1;
      }
      return f1.compareTo(f2);
   }

}
