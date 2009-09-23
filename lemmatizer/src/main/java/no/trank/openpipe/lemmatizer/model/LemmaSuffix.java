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

/**
 * @version $Revision$
 */
public class LemmaSuffix {
   private final int cut;
   private final CharSequence suffix;

   public LemmaSuffix(int cut, CharSequence suffix) {
      this.cut = cut;
      this.suffix = suffix;
   }

   public int getCut() {
      return cut;
   }

   public CharSequence getSuffix() {
      return suffix;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      final LemmaSuffix that = (LemmaSuffix) o;

      if (cut != that.cut) {
         return false;
      }
      return !(suffix != null ? !suffix.equals(that.suffix) : that.suffix != null);
   }

   @Override
   public int hashCode() {
      return 31 * cut + (suffix != null ? suffix.hashCode() : 0);
   }

   @Override
   public String toString() {
      return "LemmaSuffix{" +
            "cut=" + cut +
            ", suffix=" + suffix +
            '}';
   }
}
