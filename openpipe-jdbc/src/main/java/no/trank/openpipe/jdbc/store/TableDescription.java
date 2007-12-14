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
package no.trank.openpipe.jdbc.store;

/**
 * A class that represents a description of a database table.
 *
 * @version $Revision$
 */
public interface TableDescription {

   /**
    * Gets the name of the table.
    *
    * @return the name of the table.
    */
   public String getTableName();

   /**
    * Gets the name of the <tt>ID</tt> column.
    *
    * @return the name of the <tt>ID</tt> column.
    */
   public String getIdColumnName();

   /**
    * Gets the max length of the column named by {@link #getIdColumnName() idColumnName}.
    *
    * @return the max length of the column named by {@link #getIdColumnName() idColumnName}.
    */
   public int getIdMaxLength();

   /**
    * Gets the name of the <tt>lastUpdated</tt> column.
    *
    * @return the name of the <tt>lastUpdated</tt> column.
    */
   public String getUpdColumnName();
}
