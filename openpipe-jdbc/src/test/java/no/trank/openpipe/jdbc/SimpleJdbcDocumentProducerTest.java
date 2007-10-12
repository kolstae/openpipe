package no.trank.openpipe.jdbc;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;
import org.hsqldb.jdbcDriver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;

/**
 * @version $Revision: 874 $
 */
public class SimpleJdbcDocumentProducerTest extends TestCase {
   private static final String SQL_QUERY = "SELECT text, id FROM documents WHERE status = ";
   private static final String SQL_PRE = "INSERT INTO status_count (status, s_count) VALUES (";
   private static final String SQL_POST = "INSERT INTO status_ts (status, done_ts) VALUES (";
   private static final Map<String, Short> opStatusMap = new HashMap<String, Short>();
   private static final short STATUS_ADD = (short) 0;
   private static final short STATUS_MODIFY = (short) 1;
   private static final short STATUS_DELETE = (short) 2;
   private static final int COUNT = 50;
   private final int[] statusCounts = new int[3];
   private JdbcTemplate jdbcTemplate;
   private SingleConnectionDataSource dataSource;
   private int nextId;

   public void testEmptyIterator() throws Exception {
      final SimpleJdbcDocumentProducer producer = new SimpleJdbcDocumentProducer();
      producer.init();
      final Iterator<Document> iterator = producer.iterator();
      assertNotNull(iterator);
      assertFalse(iterator.hasNext());
   }
   
   public void testIterator() throws Exception {
      try {
         setUpDb();
         final SimpleJdbcDocumentProducer producer = new SimpleJdbcDocumentProducer();
         producer.setJdbcTemplate(jdbcTemplate);
         producer.setAddPreSql(Arrays.asList(SQL_PRE + STATUS_ADD + ", " + statusCounts[STATUS_ADD] + ")"));
         producer.setAddSql(Arrays.asList(SQL_QUERY + STATUS_ADD));
         producer.setAddPostSql(Arrays.asList(SQL_POST + STATUS_ADD + ", NOW())"));
         producer.setModifyPreSql(Arrays.asList(SQL_PRE + STATUS_MODIFY + ", " + statusCounts[STATUS_MODIFY] + ")"));
         producer.setModifySql(Arrays.asList(SQL_QUERY + STATUS_MODIFY));
         producer.setModifyPostSql(Arrays.asList(SQL_POST + STATUS_MODIFY + ", NOW())"));
         producer.setDeletePreSql(Arrays.asList(SQL_PRE + STATUS_DELETE + ", " + statusCounts[STATUS_DELETE] + ")"));
         producer.setDeleteSql(Arrays.asList(SQL_QUERY + STATUS_DELETE));
         producer.setDeletePostSql(Arrays.asList(SQL_POST + STATUS_DELETE + ", NOW())"));
         producer.init();
         final int[] counts = new int[3];
         for (Document doc : producer) {
            counts[getStatus(doc)]++;
         }
         for (int i = 0; i < counts.length; i++) {
            assertEquals(statusCounts[i], counts[i]);
            assertEquals(statusCounts[i], jdbcTemplate.queryForInt("SELECT s_count FROM status_count WHERE status = ?", 
                  new Object[]{i}));
            assertNotNull(jdbcTemplate.queryForObject("SELECT done_ts FROM status_ts WHERE status = ?", new Object[]{i}, 
                  Timestamp.class));
         }
      } finally {
         if (dataSource != null) {
            dataSource.destroy();
         }
      }
   }

   private static short getStatus(Document doc) {
      final Short status = opStatusMap.get(doc.getOperation());
      if (status == null) {
         fail("Unknown opertation: " + doc.getOperation());
      }
      return status;
   }

   protected void setUpDb() throws Exception {
      dataSource = new SingleConnectionDataSource();
      dataSource.setDriverClassName(jdbcDriver.class.getName());
      dataSource.setUrl("jdbc:hsqldb:mem:test");
      dataSource.setSuppressClose(true);
      jdbcTemplate = new JdbcTemplate(dataSource);
      jdbcTemplate.execute("CREATE TABLE documents (text VARCHAR(128), id INTEGER PRIMARY KEY, status SMALLINT)");
      jdbcTemplate.execute("CREATE TABLE status_count (status SMALLINT PRIMARY KEY, s_count INTEGER)");
      jdbcTemplate.execute("CREATE TABLE status_ts (status SMALLINT PRIMARY KEY, done_ts TIMESTAMP)");
      final Random rnd = new Random();
      for (int i = 0; i < COUNT; i++) {
         final int r = rnd.nextInt(100);
         final short status = r >= 50 ? STATUS_ADD : r < 25 ? STATUS_DELETE : STATUS_MODIFY;
         statusCounts[status]++;
         insert(status);
      }
   }

   private void insert(short status) {
      final int id = nextId++;
      jdbcTemplate.update("INSERT INTO documents (id, text, status) VALUES (?, ?, ?)", 
            new Object[]{id, "Some text [" + id + "]", status});
   }
   
   static {
      opStatusMap.put(DocumentOperation.ADD_VALUE, STATUS_ADD);
      opStatusMap.put(DocumentOperation.MODIFY_VALUE, STATUS_MODIFY);
      opStatusMap.put(DocumentOperation.DELETE_VALUE, STATUS_DELETE);
   }
}