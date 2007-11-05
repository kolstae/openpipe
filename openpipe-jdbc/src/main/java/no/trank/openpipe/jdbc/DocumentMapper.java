package no.trank.openpipe.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import no.trank.openpipe.api.document.Document;

/**
 * An interface for mapping a row in a {@link ResultSet} to a {@link Document}. 
 * 
 * @version $Revision$
 */
public interface DocumentMapper extends ParameterizedRowMapper<Document> {

   /**
    * Resets the mapper for the processing of a new <tt>ResultSet</tt>.
    * 
    * @param metaData the meta-data of the next <tt>ResultSet</tt> to be processed.
    * 
    * @throws SQLException if a problem occured.
    */
   void reset(ResultSetMetaData metaData) throws SQLException;

   @Override
   Document mapRow(ResultSet rs, int rowNum) throws SQLException;
}
