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
package no.trank.openpipe.api.document;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @version $Revision$
 */
public class FileRawData implements RawData {
   private final List<Closeable> closeables = new LinkedList<Closeable>();
   private final File file;
   private boolean released;

   public FileRawData(File file) {
      this.file = file;
   }

   public File getFile() {
      return file;
   }

   @Override
   public InputStream getInputStream() throws IOException {
      final FileInputStream fin = new FileInputStream(file);
      closeables.add(fin);
      return fin;
   }

   @Override
   public int getLength() {
      return (int) file.length();
   }

   @Override
   public void release() {
      released = true;
      for (Closeable closeable : closeables) {
         try {
            closeable.close();
         } catch (IOException e) {
            // Ignoring
         }
      }
      closeables.clear();
   }

   @Override
   public boolean isReleased() {
      return released;
   }
}
