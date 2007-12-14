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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import javax.sql.DataSource;

import org.apache.ws.jaxme.sqls.BooleanConstraint;
import org.apache.ws.jaxme.sqls.Column;
import static org.apache.ws.jaxme.sqls.Column.Type.TIMESTAMP;
import static org.apache.ws.jaxme.sqls.Column.Type.VARCHAR;
import org.apache.ws.jaxme.sqls.ColumnReference;
import org.apache.ws.jaxme.sqls.ConstrainedStatement;
import org.apache.ws.jaxme.sqls.DeleteStatement;
import org.apache.ws.jaxme.sqls.InsertStatement;
import org.apache.ws.jaxme.sqls.SQLFactory;
import org.apache.ws.jaxme.sqls.SQLGenerator;
import org.apache.ws.jaxme.sqls.Schema;
import org.apache.ws.jaxme.sqls.SelectStatement;
import org.apache.ws.jaxme.sqls.StringColumn;
import org.apache.ws.jaxme.sqls.Table;
import org.apache.ws.jaxme.sqls.UpdateStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * A class that tracks an object by its id with a backing database.
 *
 * @version $Revision$
 */
public class IdStateHolder {
   private static final Logger log = LoggerFactory.getLogger(IdStateHolder.class);
   private static final StringRowMapper STRING_ROWMAPPER = new StringRowMapper();
   private final SimpleJdbcTemplate jdbcTemplate;
   private final DataSourceTransactionManager transactionManager;
   private final SQLFactory sqlFactory;
   private SqlPSCreator update;
   private SqlPSCreator insert;
   private String delete;
   private String selectDel;
   private Timestamp startTime;
   private TransactionStatus status;

   /**
    * Creates an id state holder with the given configuration.<br/>
    * A {@link SimpleJdbcTemplate} and {@link DataSourceTransactionManager} is created from the given
    * <tt>dataSource</tt>.<br/>
    * A table matching <tt>desc</tt> is created using SQL's created by <tt>sqlFactory</tt> if one doesn't already
    * exists. If a table matching {@link TableDescription#getTableName() desc.getTableName()} already exists, it is
    * validated against <tt>desc</tt>.
    *
    * @param dataSource the datasource to use.
    * @param sqlFactory the factory to use for creating SQL's.
    * @param desc the description of the table to use.
    */
   public IdStateHolder(DataSource dataSource, SQLFactory sqlFactory, TableDescription desc) {
      jdbcTemplate = new SimpleJdbcTemplate(dataSource);
      transactionManager = new DataSourceTransactionManager(dataSource);
      this.sqlFactory = sqlFactory;
      final JdbcOperations op = jdbcTemplate.getJdbcOperations();
      final Schema schema = (Schema) op.execute(new SchemaCallback(sqlFactory));
      Table table = schema.getTable(desc.getTableName());
      final SQLGenerator generator = sqlFactory.newSQLGenerator();
      final StringColumn colId;
      final Column colUpd;
      if (table != null) {
         final Column id = table.getColumn(desc.getIdColumnName());
         colUpd = table.getColumn(desc.getUpdColumnName());
         validateTable(id, colUpd, desc.getIdMaxLength());
         colId = (StringColumn) id;
      } else {
         table = schema.newTable(desc.getTableName());
         colId = (StringColumn) table.newColumn(desc.getIdColumnName(), VARCHAR);
         colUpd = table.newColumn(desc.getUpdColumnName(), TIMESTAMP);
         colId.setNullable(false);
         colId.setLength(desc.getIdMaxLength());
         table.newPrimaryKey().addColumn(colId);
         colUpd.setNullable(false);
         createTable(op, table, generator);
      }
      createSqls(table, generator, colId, colUpd);
   }

   private void createSqls(Table table, SQLGenerator generator, StringColumn colId, Column colUpd) {
      update = createUpdate(generator, colId, colUpd, table);
      insert = createInsert(generator, colId, colUpd, table);
      delete = createDelete(generator, colUpd, table);
      selectDel = createSelectDeleted(generator, colId, colUpd, table);
      log.debug(update.getSql());
      log.debug(insert.getSql());
      log.debug(delete);
      log.debug(selectDel);
   }

   /**
    * Prepares for a set of objects. Updates {@link #getStartTime() startTime} and starts a transaction with the
    * database.
    */
   public void prepare() {
      startTime = new Timestamp(System.currentTimeMillis());
      status = transactionManager.getTransaction(new DefaultTransactionDefinition());
   }

   /**
    * Commits the transaction started in {@link #prepare()} after deleted ids has been deleted from the database.
    *
    * @see #findDeletedIds()
    */
   public void commit() {
      jdbcTemplate.update(delete, startTime);
      try {
         transactionManager.commit(status);
      } finally {
         status = null;
      }
   }

   /**
    * Rolls back the transaction started in {@link #prepare()}.
    */
   public void rollback() {
      try {
         transactionManager.rollback(status);
      } finally {
         status = null;
      }
   }

   @SuppressWarnings({"unchecked"})
   private static void createTable(JdbcOperations op, Table table, SQLGenerator generator) {
      log.debug("Creating table: {}", table.getName());
      final Collection<String> create = generator.getCreate(table, true);
      for (String sql : create) {
         log.debug(sql);
         op.execute(sql);
      }
   }

   private static void validateTable(Column colId, Column colUpd, int idMaxLength) {
      if (!colId.isStringColumn()) {
         throw new TypeMismatchDataAccessException("Type for " + colId.getName() + " was: '" +
               colId.getType().getName() + "' should be '" + VARCHAR.getName() + '\'');
      }
      final StringColumn id = (StringColumn) colId;
      if (id.getLength() < idMaxLength) {
         throw new TypeMismatchDataAccessException("Length for " + colId.getName() + " was: " + id.getLength() +
               " should be at least " + idMaxLength);
      }
      if (!TIMESTAMP.equals(colUpd.getType())) {
         throw new TypeMismatchDataAccessException("Type for " + colUpd.getName() + " was: '" +
               colUpd.getType().getName() + "' should be '" + TIMESTAMP.getName() + '\'');
      }
   }

   private SqlPSCreator createUpdate(SQLGenerator generator, StringColumn colId, Column colUpd, Table table) {
      final UpdateStatement update = sqlFactory.newUpdateStatement();
      update.setTable(table);
      update.addSet(colUpd);
      createConstraintId(colId, update);
      return new SqlPSCreator(generator.getQuery(update));
   }

   private static void createConstraintId(StringColumn colId, ConstrainedStatement statement) {
      final BooleanConstraint constraintId = statement.getWhere().createEQ();
      constraintId.addPart(statement.getTableReference().newColumnReference(colId));
      constraintId.addPlaceholder();
   }

   private String createDelete(SQLGenerator generator, Column colUpd, Table table) {
      final DeleteStatement delete = sqlFactory.newDeleteStatement();
      delete.setTable(table);
      createConstraintUpd(colUpd, delete);
      return generator.getQuery(delete);
   }

   private String createSelectDeleted(SQLGenerator generator, StringColumn colId, Column colUpd, Table table) {
      final SelectStatement select = sqlFactory.newSelectStatement();
      select.setTable(table);
      select.addResultColumn(select.getSelectTableReference().newColumnReference(colId));
      createConstraintUpd(colUpd, select);
      return generator.getQuery(select);
   }

   private static void createConstraintUpd(Column colUpd, ConstrainedStatement statement) {
      final ColumnReference colRefId = statement.getTableReference().newColumnReference(colUpd);
      final BooleanConstraint constraintId = statement.getWhere().createLT();
      constraintId.addPart(colRefId);
      constraintId.addPlaceholder();
   }

   private SqlPSCreator createInsert(SQLGenerator generator, StringColumn colId, Column colUpd, Table table) {
      final InsertStatement insert = sqlFactory.newInsertStatement();
      insert.setTable(table);
      insert.addSet(colUpd);
      insert.addSet(colId);
      return new SqlPSCreator(generator.getQuery(insert));
   }

   /**
    * Gets the timestamp created in {@link #prepare()}.
    *
    * @return the timestamp created in {@link #prepare()}.
    */
   public Timestamp getStartTime() {
      return startTime;
   }

   /**
    * Finds the ids of objects in the database, that was not updated with {@link #isUpdate(String) isUpdate(id)} since
    * last call to {@link #prepare()}.
    *
    * @return the ids of deleted documents.
    */
   public Iterator<String> findDeletedIds() {
      return jdbcTemplate.query(selectDel, STRING_ROWMAPPER, startTime).iterator();
   }

   /**
    * Checks if an object with the given id is already in the database.<br/>
    * If no such id existed, then a new record is inserted.
    * If id exists, then the record is updated with current timestamp.
    *
    * @param id the id of the object to check.
    *
    * @return <tt>true</tt> if id exists in database.
    */
   public boolean isUpdate(final String id) {
      final UpdatePSCallback callback = new UpdatePSCallback(startTime, id);
      final boolean wasUpdate = (Boolean) jdbcTemplate.getJdbcOperations().execute(update, callback);
      if (!wasUpdate) {
         jdbcTemplate.getJdbcOperations().execute(insert, callback);
      }
      return wasUpdate;
   }

   private static class SchemaCallback implements ConnectionCallback {
      private final SQLFactory sqlFactory;

      public SchemaCallback(SQLFactory sqlFactory) {
         this.sqlFactory = sqlFactory;
      }

      @Override
      public Object doInConnection(Connection con) throws SQLException, DataAccessException {
         return sqlFactory.getSchema(con, (Schema.Name) null);
      }
   }

   private static final class SqlPSCreator implements PreparedStatementCreator {
      private final String sql;

      public SqlPSCreator(String sql) {
         this.sql = sql;
      }

      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
         return con.prepareStatement(sql);
      }

      public String getSql() {
         return sql;
      }
   }

   private static final class UpdatePSCallback implements PreparedStatementCallback {
      private final Timestamp startTime;
      private final String id;

      public UpdatePSCallback(Timestamp startTime, String id) {
         this.startTime = startTime;
         this.id = id;
      }

      @Override
      public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
         ps.setTimestamp(1, startTime);
         ps.setString(2, id);
         return ps.executeUpdate() == 1;
      }
   }
}
