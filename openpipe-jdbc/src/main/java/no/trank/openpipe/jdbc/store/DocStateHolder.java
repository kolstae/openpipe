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
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @version $Revision$
 */
public class DocStateHolder {
   private static final Logger log = LoggerFactory.getLogger(DocStateHolder.class);
   private static final StringRowMapper STRING_ROWMAPPER = new StringRowMapper();
   private final SimpleJdbcTemplate jdbcTemplate;
   private DataSourceTransactionManager transactionManager;
   private final SQLFactory sqlFactory;
   private String update;
   private String insert;
   private String delete;
   private String selectDel;
   private Timestamp startTime;
   private TransactionStatus status;

   public DocStateHolder(DataSource dataSource, SQLFactory sqlFactory, TableDescription desc) {
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
      update = createUpdate(generator, colId, colUpd, table);
      insert = createInsert(generator, colId, colUpd, table);
      delete = createDelete(generator, colUpd, table);
      selectDel = createSelectDeleted(generator, colId, colUpd, table);
      log.debug(update);
      log.debug(insert);
      log.debug(delete);
      log.debug(selectDel);
   }

   public void prepare() {
      startTime = new Timestamp(System.currentTimeMillis());
      status = transactionManager.getTransaction(new DefaultTransactionDefinition());
   }

   public void commit() {
      jdbcTemplate.update(delete, startTime);
      try {
         transactionManager.commit(status);
      } finally {
         status = null;
      }
   }

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

   private String createUpdate(SQLGenerator generator, StringColumn colId, Column colUpd, Table table) {
      final UpdateStatement update = sqlFactory.newUpdateStatement();
      update.setTable(table);
      update.addSet(colUpd);
      createConstraintId(colId, update);
      return generator.getQuery(update);
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

   private String createInsert(SQLGenerator generator, StringColumn colId, Column colUpd, Table table) {
      final InsertStatement insert = sqlFactory.newInsertStatement();
      insert.setTable(table);
      insert.addSet(colId);
      insert.addSet(colUpd);
      return generator.getQuery(insert);
   }

   public Iterator<String> findDeletedIds() {
      return jdbcTemplate.query(selectDel, STRING_ROWMAPPER, startTime).iterator();
   }

   public boolean isUpdate(String id) {
      final boolean wasUpdate = jdbcTemplate.update(update, startTime, id) == 1;
      if (!wasUpdate) {
         jdbcTemplate.update(insert, id, startTime);
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

}
