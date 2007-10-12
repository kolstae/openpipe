package no.trank.openpipe.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import static no.trank.openpipe.api.document.DocumentOperation.*;

/**
 * @version $Revision: 874 $
 */
public class SimpleJdbcDocumentProducer extends JdbcDocumentProducer {
   private static final Logger log = LoggerFactory.getLogger(SimpleJdbcDocumentProducer.class);
   private final Map<String, SqlOperationPart> opPartsMap = new LinkedHashMap<String, SqlOperationPart>();

   @Override
   public void init() {
      super.setOperationParts(new ArrayList<OperationPart>(opPartsMap.values()));
      super.init();
   }

   private static <T> List<T> notNull(List<T> list) {
      return list == null ? Collections.<T>emptyList() : list;
   }

   private static void execute(JdbcTemplate jdbcTemplate, List<String> sqls) throws DataAccessException {
      for (String sql : sqls) {
         log.debug("Executing sql {}", sql);
         jdbcTemplate.execute(sql);
      }
   }

   private SqlOperationPart getPart(String operation) {
      SqlOperationPart part = opPartsMap.get(operation);
      if (part == null) {
         part = new SqlOperationPart(operation);
         opPartsMap.put(operation, part);
      }
      return part;
   }

   public void setPreSql(String operation, List<String> sql) {
      getPart(operation).setPreSql(sql);
   }

   public void setSql(String operation, List<String> sql) {
      getPart(operation).setSqls(sql);
   }

   public void setPostSql(String operation, List<String> sql) {
      getPart(operation).setPostSql(sql);
   }

   public void setAddPreSql(List<String> sql) {
      setPreSql(ADD_VALUE, sql);
   }

   public void setAddSql(List<String> sql) {
      setSql(ADD_VALUE, sql);
   }

   public void setAddPostSql(List<String> sql) {
      setPostSql(ADD_VALUE, sql);
   }

   public void setModifyPreSql(List<String> sql) {
      setPreSql(MODIFY_VALUE, sql);
   }

   public void setModifySql(List<String> sql) {
      setSql(MODIFY_VALUE, sql);
   }

   public void setModifyPostSql(List<String> sql) {
      setPostSql(MODIFY_VALUE, sql);
   }

   public void setDeletePreSql(List<String> sql) {
      setPreSql(DELETE_VALUE, sql);
   }

   public void setDeleteSql(List<String> sql) {
      setSql(DELETE_VALUE, sql);
   }

   public void setDeletePostSql(List<String> sql) {
      setPostSql(DELETE_VALUE, sql);
   }

   private static class SqlOperationPart implements OperationPart {
      private final String operation;
      private List<String> preSql = Collections.emptyList();
      private List<String> sqls = Collections.emptyList();
      private List<String> postSql = Collections.emptyList();

      public SqlOperationPart(String operation) {
         this.operation = operation;
      }

      public boolean isEmpty() {
         return preSql.isEmpty() && sqls.isEmpty() && postSql.isEmpty();
      }

      public String getOperation() {
         return operation;
      }

      public void doPreSql(JdbcTemplate jdbcTemplate) throws DataAccessException {
         execute(jdbcTemplate, preSql);
      }

      public void doPostSql(JdbcTemplate jdbcTemplate) throws DataAccessException {
         execute(jdbcTemplate, postSql);
      }

      public List<String> getSqls() {
         return sqls;
      }

      public void setSqls(List<String> sqls) {
         this.sqls = notNull(sqls);
      }

      public void setPreSql(List<String> preSql) {
         this.preSql = notNull(preSql);
      }

      public void setPostSql(List<String> postSql) {
         this.postSql = notNull(postSql);
      }
   }
}