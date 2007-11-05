package no.trank.openpipe.reader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;
import no.trank.openpipe.api.document.DocumentProducer;
import no.trank.openpipe.api.document.FileRawData;
import no.trank.openpipe.util.AcceptAllFileFilter;
import no.trank.openpipe.util.FilesFirstComparator;
import no.trank.openpipe.util.RegexFileFilter;

/**
 * @version $Revision$
 */
public class FileDocumentReader implements DocumentProducer {
   private static final FileFilter ACCEPT_ALL = new AcceptAllFileFilter();
   private static final Comparator<File> FILES_FIRST_COMPARATOR = new FilesFirstComparator();
   private FileDocReader reader;
   private File directory;
   private String fileNameField = "fileName";
   private String pathField = "pathName";
   private int maxDepth = 1;
   private FileFilter filter;
   private String regexPattern;

   @Override
   public final void init() {
      if (!directory.isDirectory()) {
         throw new IllegalArgumentException("'" + directory + "' is not a directory");
      }
      if (!directory.canRead()) {
         throw new IllegalArgumentException("Can not read directory '" + directory + "'");
      }
      if (filter == null && regexPattern != null) {
         filter = new RegexFileFilter(regexPattern);
      }
      reader = createReader();
   }

   @Override
   public void close() {
      reader = null;
   }

   protected FileDocReader createReader() {
      return new FileDocReader() {
         @Override
         public Document getDocument(File file) {
            return new Document(new FileRawData(file));
         }
      };
   }

   @Override
   public Iterator<Document> iterator() {
      final int depth = maxDepth < 0 ? Integer.MAX_VALUE : maxDepth;
      return new FileIterator(directory, depth, new DocReader(reader, fileNameField, pathField), filter);
   }

   public void setDirectory(String directory) {
      this.directory = new File(directory).getAbsoluteFile();
   }

   public String getDirectory() {
      return directory.getPath();
   }

   public String getFileNameField() {
      return fileNameField;
   }

   public void setFileNameField(String fileNameField) {
      this.fileNameField = fileNameField;
   }

   public String getPathField() {
      return pathField;
   }

   public void setPathField(String pathField) {
      this.pathField = pathField;
   }

   public int getMaxDepth() {
      return maxDepth;
   }

   /**
    * Sets the max depth to go into directory structure. Setting <tt>maxDepth &lt; 0</tt> means no limit. Default value 
    * is <tt>1</tt>. 
    * 
    * @param maxDepth the max depth to go into directory structure.
    */
   public void setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
   }

   public FileFilter getFilter() {
      return filter;
   }

   public void setFilter(FileFilter filter) {
      this.filter = filter;
   }

   public String getRegexPattern() {
      return regexPattern;
   }

   public void setRegexPattern(String regexPattern) {
      this.regexPattern = regexPattern;
   }

   public static interface FileDocReader {
      Document getDocument(File file);
   }

   private static class DocReader implements  FileDocReader {
      private final FileDocReader reader;
      private final String fileNameField;
      private final String pathField;

      public DocReader(FileDocReader reader, String fileNameField, String pathField) {
         this.reader = reader;
         this.fileNameField = fileNameField;
         this.pathField = pathField;
      }

      @Override
      public Document getDocument(File file) {
         final Document doc = reader.getDocument(file);
         if (fileNameField != null) {
            doc.setFieldValue(fileNameField, file.getName());
         }
         if (pathField != null) {
            doc.setFieldValue(pathField, file.getPath());
         }
         if (doc.getOperation() == null) {
            doc.setOperation(DocumentOperation.ADD_VALUE);
         }
         return doc;
      }
   }

   private static class FileIterator implements Iterator<Document> {
      private final FileDocReader reader;
      private final FileFilter fileFilter;
      private final Deque<Iterator<File>> stack;
      private final int maxDepth;
      private Iterator<File> fileIt;
      private File file;
      private final FileOnlyFileFilter onlyFileFilter;

      public FileIterator(File file, int maxDepth, FileDocReader reader, FileFilter fileFilter) {
         this.reader = reader;
         if (fileFilter != null) {
            this.fileFilter = fileFilter;
         } else {
            this.fileFilter = ACCEPT_ALL;
         }
         this.maxDepth = maxDepth - 1;
         stack = new ArrayDeque<Iterator<File>>();
         fileIt = getFiles(file);
         onlyFileFilter = new FileOnlyFileFilter(this.fileFilter);
      }

      private Iterator<File> getFiles(File file) {
         final File[] files;
         if (stack.size() >= maxDepth) {
            files = file.listFiles(onlyFileFilter);
            Arrays.sort(files);
         } else {
            files = file.listFiles(fileFilter);
            Arrays.sort(files, FILES_FIRST_COMPARATOR);
         }
         return Arrays.asList(files).iterator();
      }

      @Override
      public boolean hasNext() {
         findNextFile();
         return file != null;
      }

      private void findNextFile() {
         if (file == null) {
            if (findFileIt()) {
               file = fileIt.next();
               while (file != null && file.isDirectory()) {
                  stack.push(fileIt);
                  fileIt = getFiles(file);
                  file = findFileIt() ? fileIt.next() : null;
               }
            }
         }
      }

      private boolean findFileIt() {
         while (!fileIt.hasNext() && !stack.isEmpty()) {
            fileIt = stack.pop();
         }
         return fileIt.hasNext();
      }

      @Override
      public Document next() {
         if (!hasNext()) {
            throw new NoSuchElementException();
         }
         try {
            return reader.getDocument(file);
         } finally {
            file = null;
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

   }

   private static class FileOnlyFileFilter implements FileFilter {
      private final FileFilter fileFilter;

      public FileOnlyFileFilter(FileFilter fileFilter) {
         this.fileFilter = fileFilter;
      }

      @Override
      public boolean accept(File file) {
         return !file.isDirectory() && fileFilter.accept(file);
      }

   }
}
