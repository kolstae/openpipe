package no.trank.openpipe.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @version $Revision: 874 $
 */
public class AcceptAllFileFilter implements FileFilter {
   public boolean accept(File pathname) {
      return true;
   }
}
