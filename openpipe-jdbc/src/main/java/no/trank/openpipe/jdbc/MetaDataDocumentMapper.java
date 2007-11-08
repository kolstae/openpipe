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
package no.trank.openpipe.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import no.trank.openpipe.api.document.Document;

/**
 * A class that maps a column in a {@link ResultSet} using {@link ResultSetMetaData#getColumnLabel(int)} as field-name 
 * in the {@link Document}.  
 * 
 * @version $Revision$
 */
public class MetaDataDocumentMapper implements DocumentMapper {
   private String[] colNames;

   @Override
   public void reset(ResultSetMetaData metaData) throws SQLException {
      final int columnCount = metaData.getColumnCount();
      colNames = new String[columnCount];
      for (int i = 0; i < columnCount; i++) {
         colNames[i] = metaData.getColumnLabel(i + 1);
      }
   }

   @Override
   public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
      final Document doc = new Document();
      for (int i = 0; i < colNames.length; i++) {
         doc.setFieldValue(colNames[i], rs.getString(i + 1));
      }
      return doc;
   }
}
