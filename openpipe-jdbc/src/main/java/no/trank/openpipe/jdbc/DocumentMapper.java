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
