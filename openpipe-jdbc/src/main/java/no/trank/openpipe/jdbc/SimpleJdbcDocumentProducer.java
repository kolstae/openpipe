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
 * A simple document producer for getting documents from a jdbc source.
 *
 * The producer can trigger add, modify and/or deletes. Each operation has a corresponding pre, post, fail and list
 * sqls. Ex: for add you have the setters: setAddPreSql(...), setAddSql(...), setAddPostSql(...) and
 * setAddFailSql(List<String> sql).
 *
 * For each operation type (add, modify, delete):<br/>
 * - The preSql is run<br/>
 * - Each document is produced by running the producer sql. There is no guarantee that all documents are fetched before
 * next step happens. But it is guaranteed that a after a post/failSql a new preSql is run before new documents are
 * fetched.<br/>
 * - If no errors happened the postSql is run.</br>
 * - If an error happened the failSql is run.</br>
 *
 * To use:
 *
 * 1. Set the JdbcTemplate (and its dataSource)</br>
 * 2. Set the sqls for producing documents.</br>
 *
 * @version $Revision$
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

   /**
    * Set a list of preSqls to run.
    *
    * @param operation the operation that this preSql is used for.
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setPreSql(String operation, List<String> sql) {
      getPart(operation).setPreSql(sql);
   }

   /**
    * Set a list of sqls to run to get the documents.
    *
    * @param operation the operation that this preSql is used for.
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setSql(String operation, List<String> sql) {
      getPart(operation).setSqls(sql);
   }

   /**
    * Set a list of sqls to run after documents have been fetched.
    *
    * @param operation the operation that this preSql is used for.
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setPostSql(String operation, List<String> sql) {
      getPart(operation).setPostSql(sql);
   }

   /**
    * Set a list of sqls to run if there was an error in the pipeline.
    *
    * @param operation the operation that this preSql is used for.
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   private void setFailSql(String operation, List<String> sql) {
      getPart(operation).setFailSql(sql);      
   }

   /**
    * Set a list of preSqls to run before producing add Documents.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setAddPreSql(List<String> sql) {
      setPreSql(ADD_VALUE, sql);
   }

   /**
    * Set a list of sqls to run for producing add Documents.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setAddSql(List<String> sql) {
      setSql(ADD_VALUE, sql);
   }

   /**
    * Set a list of sqls to run after add documents have been produced. (If no error happened)
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setAddPostSql(List<String> sql) {
      setPostSql(ADD_VALUE, sql);
   }

   /**
    * Set a list of sqls to run after add documents have been produced and some error happened.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setAddFailSql(List<String> sql) {
      setFailSql(ADD_VALUE, sql);
   }

   /**
    * Set a list of preSqls to run before producing modify Documents.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setModifyPreSql(List<String> sql) {
      setPreSql(MODIFY_VALUE, sql);
   }

   /**
    * Set a list of sqls to run for producing modify Documents.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setModifySql(List<String> sql) {
      setSql(MODIFY_VALUE, sql);
   }

   /**
    * Set a list of sqls to run after modify documents have been produced. (If no error happened)
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setModifyPostSql(List<String> sql) {
      setPostSql(MODIFY_VALUE, sql);
   }

   /**
    * Set a list of sqls to run after modify documents have been produced and some error happened.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setModifyFailSql(List<String> sql) {
      setFailSql(MODIFY_VALUE, sql);
   }

   /**
    * Set a list of preSqls to run before producing delete Documents.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setDeletePreSql(List<String> sql) {
      setPreSql(DELETE_VALUE, sql);
   }

   /**
    * Set a list of sqls to run for producing delete Documents.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setDeleteSql(List<String> sql) {
      setSql(DELETE_VALUE, sql);
   }

   /**
    * Set a list of sqls to run after delete documents have been produced. (If no error happened)
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */
   public void setDeletePostSql(List<String> sql) {
      setPostSql(DELETE_VALUE, sql);
   }

   /**
    * Set a list of sqls to run after delete documents have been produced and some error happened.
    *
    * @param sql a list of sql to run as preSql
    * @see no.trank.openpipe.api.document.DocumentOperation
    */      
   public void setDeleteFailSql(List<String> sql) {
      setFailSql(DELETE_VALUE, sql);
   }


   private static class SqlOperationPart implements OperationPart {
      private final String operation;
      private List<String> preSql = Collections.emptyList();
      private List<String> sqls = Collections.emptyList();
      private List<String> postSql = Collections.emptyList();
      private List<String> failSql = Collections.emptyList();

      public SqlOperationPart(String operation) {
         this.operation = operation;
      }

      @Override
      public boolean isEmpty() {
         return preSql.isEmpty() && sqls.isEmpty() && postSql.isEmpty();
      }

      @Override
      public String getOperation() {
         return operation;
      }

      @Override
      public void doPreSql(JdbcTemplate jdbcTemplate) throws DataAccessException {
         execute(jdbcTemplate, preSql);
      }

      @Override
      public void doPostSql(JdbcTemplate jdbcTemplate) throws DataAccessException {
         execute(jdbcTemplate, postSql);
      }

      @Override
      public void doFailSql(JdbcTemplate jdbcTemplate) throws DataAccessException {
         execute(jdbcTemplate, failSql);
      }

      @Override
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

      public void setFailSql(List<String> failSql) {
         this.failSql = notNull(failSql);
      }
   }
}