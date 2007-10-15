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

   public void reset(ResultSetMetaData metaData) throws SQLException {
      final int columnCount = metaData.getColumnCount();
      colNames = new String[columnCount];
      for (int i = 0; i < columnCount; i++) {
         colNames[i] = metaData.getColumnLabel(i + 1);
      }
   }

   public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
      final Document doc = new Document();
      for (int i = 0; i < colNames.length; i++) {
         doc.setFieldValue(colNames[i], rs.getString(i + 1));
      }
      return doc;
   }
}
