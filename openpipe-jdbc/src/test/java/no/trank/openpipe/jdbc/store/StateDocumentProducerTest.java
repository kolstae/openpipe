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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import org.apache.ws.jaxme.sqls.hsqldb.HsqlDbSQLFactoryImpl;
import static org.easymock.EasyMock.*;
import org.hsqldb.jdbcDriver;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;
import no.trank.openpipe.api.document.DocumentProducer;
import no.trank.openpipe.util.Iterators;

/**
 * @version $Revision$
 */
public class StateDocumentProducerTest extends TestCase {
   private static final String FIELD_ID = "id";
   private static final String TABLE_NAME = "doc_store";
   private static final String COL_ID_NAME = "id";
   private static final int ID_MAX_LENGTH = 256;
   private static final String COL_UPD_NAME = "updated";
   private SingleConnectionDataSource dataSource;
   private SimpleJdbcTemplate jdbcTemplate;
   private static final String DOC_ID = "1";

   public void testValidateSchemaFailes() throws Exception {
      jdbcTemplate.getJdbcOperations().execute("CREATE CACHED TABLE " + TABLE_NAME +
            " (" + COL_ID_NAME + " INTEGER NOT NULL, " +
            COL_UPD_NAME + " TIMESTAMP NOT NULL,  PRIMARY KEY (" + COL_ID_NAME + "))");
      {
         final DocumentProducer mockProd = setUpMockProducer(Iterators.<Document>emptyIterator(), false);
         final StateDocumentProducer producer = setupProducer(mockProd);
         try {
            producer.init();
            fail("Init sould fail");
         } catch (Exception e) {
            // Success
         }
      }
      dropTable();
      jdbcTemplate.getJdbcOperations().execute("CREATE CACHED TABLE " + TABLE_NAME +
            " (" + COL_ID_NAME + " VARCHAR(" + (ID_MAX_LENGTH - 1) + ") NOT NULL, " +
            COL_UPD_NAME + " TIMESTAMP NOT NULL, PRIMARY KEY (" + COL_ID_NAME + "))");
      {
         final DocumentProducer mockProd = setUpMockProducer(Iterators.<Document>emptyIterator(), false);
         final StateDocumentProducer producer = setupProducer(mockProd);
         try {
            producer.init();
            fail("Init sould fail");
         } catch (Exception e) {
            // Success
         }
      }
      dropTable();
      jdbcTemplate.getJdbcOperations().execute("CREATE CACHED TABLE " + TABLE_NAME +
            " (" + COL_ID_NAME + " VARCHAR(" + ID_MAX_LENGTH + ") NOT NULL, " +
            COL_UPD_NAME + " DATE NOT NULL, PRIMARY KEY (" + COL_ID_NAME + "))");
      {
         final DocumentProducer mockProd = setUpMockProducer(Iterators.<Document>emptyIterator(), false);
         final StateDocumentProducer producer = setupProducer(mockProd);
         try {
            producer.init();
            fail("Init sould fail");
         } catch (Exception e) {
            // Success
         }
      }
   }

   public void testValidateSchemaSucceeds() throws Exception {
      createValidTable();
      // Setting up mock
      final DocumentProducer mockProd = setUpMockProducer(Iterators.<Document>emptyIterator(), false);

      final StateDocumentProducer producer = setupProducer(mockProd);
      producer.init();
      final Iterator<Document> it = producer.iterator();
      assertFalse(it.hasNext());
      producer.close();
      verify(mockProd);
   }

   public void testStoreNewDocuments() throws Exception {
      // Setting up mock
      final Document doc = new Document();
      doc.setFieldValue(FIELD_ID, DOC_ID);
      final DocumentProducer mockProd = setUpMockProducer(Arrays.asList(doc).iterator(), false);

      final StateDocumentProducer producer = setupProducer(mockProd);
      producer.init();
      final Iterator<Document> it = producer.iterator();
      assertTrue(it.hasNext());
      assertEquals(DocumentOperation.ADD_VALUE, it.next().getOperation());
      assertFalse(it.hasNext());
      producer.close();
      verify(mockProd);
      final String id = jdbcTemplate.queryForObject(getSelectId(), new StringRowMapper(), (Object[]) null);
      assertEquals(DOC_ID, id);
   }

   public void testStoreNewDocuments_RollbackOnFail() throws Exception {
      // Setting up mock
      final Document doc = new Document();
      doc.setFieldValue(FIELD_ID, DOC_ID);
      final DocumentProducer mockProd = setUpMockProducer(Arrays.asList(doc).iterator(), true);

      final StateDocumentProducer producer = setupProducer(mockProd);
      producer.init();
      final Iterator<Document> it = producer.iterator();
      assertTrue(it.hasNext());
      assertEquals(DocumentOperation.ADD_VALUE, it.next().getOperation());
      assertFalse(it.hasNext());
      producer.fail();
      verify(mockProd);
      final List<String> id = jdbcTemplate.query(getSelectId(), new StringRowMapper(), (Object[]) null);
      assertTrue(id.isEmpty());
   }

   public void testUpdateModifiedDocuments() throws Exception {
      createValidTable();
      final Timestamp ts = new Timestamp(System.currentTimeMillis());
      jdbcTemplate.update("INSERT INTO " + TABLE_NAME + " (" + COL_ID_NAME + ", " + COL_UPD_NAME + ") VALUES (?, ?)",
            DOC_ID, ts);

      // Setting up mock
      final Document doc = new Document();
      doc.setFieldValue(FIELD_ID, DOC_ID);
      final DocumentProducer mockProd = setUpMockProducer(Arrays.asList(doc).iterator(), false);

      final StateDocumentProducer producer = setupProducer(mockProd);
      producer.init();
      final Iterator<Document> it = producer.iterator();
      assertTrue(it.hasNext());
      assertEquals(DocumentOperation.MODIFY_VALUE, it.next().getOperation());
      assertFalse(it.hasNext());
      producer.close();
      verify(mockProd);
      final String id = jdbcTemplate.queryForObject(getSelectId(), new StringRowMapper(), (Object[]) null);
      assertEquals(DOC_ID, id);
      final Timestamp upd = jdbcTemplate.queryForObject(getSelectUpd(), new TimestampRowMapper(), (Object[]) null);
      assertTrue(upd.after(ts));
   }

   public void testUpdateModifiedDocuments_RollbackOnFail() throws Exception {
      createValidTable();
      final Timestamp ts = new Timestamp(System.currentTimeMillis());
      jdbcTemplate.update("INSERT INTO " + TABLE_NAME + " (" + COL_ID_NAME + ", " + COL_UPD_NAME + ") VALUES (?, ?)",
            DOC_ID, ts);

      // Setting up mock
      final Document doc = new Document();
      doc.setFieldValue(FIELD_ID, DOC_ID);
      final DocumentProducer mockProd = setUpMockProducer(Arrays.asList(doc).iterator(), true);

      final StateDocumentProducer producer = setupProducer(mockProd);
      producer.init();
      final Iterator<Document> it = producer.iterator();
      assertTrue(it.hasNext());
      assertEquals(DocumentOperation.MODIFY_VALUE, it.next().getOperation());
      assertFalse(it.hasNext());
      producer.fail();
      verify(mockProd);
      final String id = jdbcTemplate.queryForObject(getSelectId(), new StringRowMapper(), (Object[]) null);
      assertEquals(DOC_ID, id);
      final Timestamp upd = jdbcTemplate.queryForObject(getSelectUpd(), new TimestampRowMapper(), (Object[]) null);
      assertTrue(upd.equals(ts));
   }

   public void testDeletedDocuments() throws Exception {
      createValidTable();
      final Timestamp ts = new Timestamp(System.currentTimeMillis());
      jdbcTemplate.update("INSERT INTO " + TABLE_NAME + " (" + COL_ID_NAME + ", " + COL_UPD_NAME + ") VALUES (?, ?)",
            DOC_ID, ts);

      // Setting up mock
      final DocumentProducer mockProd = setUpMockProducer(Iterators.<Document>emptyIterator(), false);

      final StateDocumentProducer producer = setupProducer(mockProd);
      producer.init();
      final Iterator<Document> it = producer.iterator();
      assertTrue(it.hasNext());
      final Document doc = it.next();
      assertEquals(DocumentOperation.DELETE_VALUE, doc.getOperation());
      assertEquals(DOC_ID, doc.getFieldValue(FIELD_ID));
      assertFalse(it.hasNext());
      producer.close();
      verify(mockProd);
      final List<String> id = jdbcTemplate.query(getSelectId(), new StringRowMapper(), (Object[]) null);
      assertTrue(id.isEmpty());
   }

   public void testDeletedDocuments_RollbackOnFail() throws Exception {
      createValidTable();
      final Timestamp ts = new Timestamp(System.currentTimeMillis());
      jdbcTemplate.update("INSERT INTO " + TABLE_NAME + " (" + COL_ID_NAME + ", " + COL_UPD_NAME + ") VALUES (?, ?)",
            DOC_ID, ts);

      // Setting up mock
      final DocumentProducer mockProd = setUpMockProducer(Iterators.<Document>emptyIterator(), true);

      final StateDocumentProducer producer = setupProducer(mockProd);
      producer.init();
      final Iterator<Document> it = producer.iterator();
      assertTrue(it.hasNext());
      final Document doc = it.next();
      assertEquals(DocumentOperation.DELETE_VALUE, doc.getOperation());
      assertEquals(DOC_ID, doc.getFieldValue(FIELD_ID));
      assertFalse(it.hasNext());
      producer.fail();
      verify(mockProd);
      final List<String> id = jdbcTemplate.query(getSelectId(), new StringRowMapper(), (Object[]) null);
      assertFalse(id.isEmpty());
   }

   private static DocumentProducer setUpMockProducer(Iterator<Document> it, boolean fail) {
      final DocumentProducer mockProd = createMock(DocumentProducer.class);
      mockProd.init();
      expectLastCall().once();
      expect(mockProd.iterator()).andReturn(it).once();
      if (fail) {
         mockProd.fail();
      } else {
         mockProd.close();
      }
      expectLastCall().once();
      replay(mockProd);
      return mockProd;
   }

   private void dropTable() {
      jdbcTemplate.getJdbcOperations().execute("DROP TABLE " + TABLE_NAME);
   }

   private void createValidTable() {
      jdbcTemplate.getJdbcOperations().execute("CREATE CACHED TABLE " + TABLE_NAME +
            " (" + COL_ID_NAME + " VARCHAR(" + ID_MAX_LENGTH + ") NOT NULL, " +
            COL_UPD_NAME + " TIMESTAMP NOT NULL,  PRIMARY KEY (" + COL_ID_NAME + "))");
   }

   private static String getSelectId() {
      return "SELECT " + COL_ID_NAME + " FROM " + TABLE_NAME;
   }

   private static String getSelectUpd() {
      return "SELECT " + COL_UPD_NAME + " FROM " + TABLE_NAME;
   }

   private StateDocumentProducer setupProducer(DocumentProducer mockProd) {
      final StateDocumentProducer producer = new StateDocumentProducer(mockProd);
      producer.setDataSource(dataSource);
      producer.setSqlFactory(new HsqlDbSQLFactoryImpl());
      producer.setIdFieldName(FIELD_ID);
      producer.setTableName(TABLE_NAME);
      producer.setIdColumnName(COL_ID_NAME);
      producer.setIdMaxLength(ID_MAX_LENGTH);
      producer.setUpdColumnName(COL_UPD_NAME);
      return producer;
   }

   @Override
   protected void setUp() throws Exception {
      dataSource = new SingleConnectionDataSource();
      dataSource.setDriverClassName(jdbcDriver.class.getName());
      dataSource.setUrl("jdbc:hsqldb:mem:test");
      dataSource.setSuppressClose(true);
      jdbcTemplate = new SimpleJdbcTemplate(dataSource);
   }

   @Override
   protected void tearDown() throws Exception {
      if (dataSource != null) {
         dropTable();
         dataSource.destroy();
      }
   }

   private static class TimestampRowMapper implements ParameterizedRowMapper<Timestamp> {
      @Override
      public Timestamp mapRow(ResultSet rs, int rowNum) throws SQLException {
         return rs.getTimestamp(1);
      }
   }
}