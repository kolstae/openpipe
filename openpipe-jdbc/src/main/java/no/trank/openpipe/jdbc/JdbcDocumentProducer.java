package no.trank.openpipe.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentProducer;
import no.trank.openpipe.util.Iterators;

/**
 * @version $Revision$
 */
public class JdbcDocumentProducer implements DocumentProducer {
   private static final Logger log = LoggerFactory.getLogger(JdbcDocumentProducer.class);
   private JdbcStats jdbcStats;
   private JdbcTemplate jdbcTemplate;
   private DocumentMapper documentMapper;
   private List<? extends OperationPart> operationParts;

   public void init() {
      if (documentMapper == null) {
         log.debug("No documentMapper provided, using MetaDataDocumentMapper");
         documentMapper = new MetaDataDocumentMapper();
      }
      
      if (jdbcStats == null) {
         log.debug("No jdbcStats provided, using NoopJdbcStats");
         jdbcStats = new NoopJdbcStats();
      }
      
      if (operationParts == null || operationParts.isEmpty()) {
         log.warn("No operationParts provided!");
      }
   }

   public void close() {
   }

   public Iterator<Document> iterator() {
      // a wrapper iterator. handles pre and post sql.
      return new SqlIterator(jdbcTemplate, notEmpty(operationParts), jdbcStats, documentMapper);
   }

   private static Iterator<OperationPart> notEmpty(List<? extends OperationPart> parts) {
      if (parts == null || parts.isEmpty()) {
         return Iterators.emptyIterator();
      }
      final List<OperationPart> list = new ArrayList<OperationPart>(parts.size());
      for (OperationPart part : parts) {
         if (!part.isEmpty()) {
            list.add(part);
         }
      }
      return list.iterator();
   }

   public List<? extends OperationPart> getOperationParts() {
      return operationParts;
   }

   public void setOperationParts(List<? extends OperationPart> operationParts) {
      this.operationParts = operationParts;
   }

   public JdbcStats getJdbcStats() {
      return jdbcStats;
   }

   public void setJdbcStats(JdbcStats jdbcStats) {
      this.jdbcStats = jdbcStats;
   }

   public JdbcTemplate getJdbcTemplate() {
      return jdbcTemplate;
   }

   public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
   }

   public DocumentMapper getDocumentMapper() {
      return documentMapper;
   }

   public void setDocumentMapper(DocumentMapper documentMapper) {
      this.documentMapper = documentMapper;
   }

   public static interface OperationPart {

      public boolean isEmpty();

      public String getOperation();

      public void doPreSql(JdbcTemplate jdbcTemplate) throws DataAccessException;

      public void doPostSql(JdbcTemplate jdbcTemplate) throws DataAccessException;

      public List<String> getSqls();
   }

   private static class SqlIterator implements Iterator<Document> {
      private final JdbcTemplate jdbcTemplate;
      private final Iterator<OperationPart> partIt;
      private final JdbcStats stats;
      private final DocumentMapper mapper;
      private OperationPart part;
      private Iterator<Document> docIt = Iterators.emptyIterator();

      public SqlIterator(JdbcTemplate jdbcTemplate, Iterator<OperationPart> parts, JdbcStats stats, 
            DocumentMapper mapper) throws DataAccessException {
         this.jdbcTemplate = jdbcTemplate;
         partIt = parts;
         this.stats = stats;
         this.mapper = mapper;
         findPart();
      }

      private void findPart() throws DataAccessException {
         endPart();
         if (part == null && partIt.hasNext()) {
            part = partIt.next();
            stats.startPreSql();
            part.doPreSql(jdbcTemplate);
            stats.startIt();
            docIt = new DocIterator(part.getSqls().iterator(), jdbcTemplate, mapper);
         }
      }

      private void endPart() throws DataAccessException {
         if (!docIt.hasNext() && part != null) {
            try {
               stats.startPostSql();
               part.doPostSql(jdbcTemplate);
               stats.stop();
            } finally {
               part = null;
            }
         }
      }

      public boolean hasNext() {
         findPart();
         return docIt.hasNext();
      }

      public Document next() {
         if (!hasNext()) {
            throw new NoSuchElementException();
         }
         final Document doc = docIt.next();
         final String op = part.getOperation();
         stats.incr(op);
         doc.setOperation(op);
         return doc;
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static class DocIterator implements Iterator<Document> {
      private final Iterator<String> sqlIt;
      private final Connection connection;
      private final SQLExceptionTranslator translator;
      private final int fetchSize;
      private final DataSource dataSource;
      private ResultSet resultSet;
      private PreparedStatement prepSt;
      private Document doc;
      private DocumentMapper mapper;
      private String sql;

      public DocIterator(Iterator<String> sqls, JdbcTemplate jdbcTemplate, DocumentMapper mapper) {
         sqlIt = sqls;
         this.mapper = mapper;
         dataSource = jdbcTemplate.getDataSource();
         connection = DataSourceUtils.getConnection(dataSource);
         translator = jdbcTemplate.getExceptionTranslator();
         fetchSize = jdbcTemplate.getFetchSize();
         findDoc();
      }

      private void findDoc() throws DataAccessException {
         while (doc == null && (resultSet != null || sqlIt.hasNext())) {
            findResults();
            try {
               if (resultSet != null) {
                  if (resultSet.next()) {
                     doc = mapper.mapRow(resultSet, -1);
                  } else if (sqlIt.hasNext()) {
                     closeCurrent();
                  } else {
                     close();
                  }
               }
            } catch (SQLException e) {
               close();
               throw translator.translate("DocIterator", sql, e);
            }
         }
      }

      private void findResults() throws DataAccessException {
         if (resultSet == null && sqlIt.hasNext()) {
            sql = sqlIt.next();
            try {
               prepSt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
               if (fetchSize > 0) {
                  prepSt.setFetchSize(fetchSize);
               }
               log.debug("Executing query {}", sql);
               resultSet = prepSt.executeQuery();
               mapper.reset(resultSet.getMetaData());
            } catch (SQLException e) {
               close();
               throw translator.translate("DocIterator", sql, e);
            }
         }
      }

      private void close() {
         closeCurrent();
         DataSourceUtils.releaseConnection(connection, dataSource);
      }

      private void closeCurrent() {
         JdbcUtils.closeResultSet(resultSet);
         JdbcUtils.closeStatement(prepSt);
         resultSet = null;
         prepSt = null;
      }

      public boolean hasNext() {
         findDoc();
         return doc != null;
      }

      public Document next() {
         if (!hasNext()) {
            throw new NoSuchElementException();
         }
         try {
            return doc;
         } finally {
            doc = null;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}