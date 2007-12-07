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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.ws.jaxme.sqls.SQLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;
import no.trank.openpipe.api.document.DocumentProducer;
import no.trank.openpipe.config.BeanValidator;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;

/**
 * @version $Revision$
 */
public class StateDocumentProducer implements DocumentProducer {
   private static final Logger log = LoggerFactory.getLogger(StateDocumentProducer.class);
   private final DocumentProducer producer;
   @NotEmpty
   private String idFieldName;
   @NotNull
   private SimpleJdbcTemplate jdbcTemplate;
   @NotNull
   private TransactionTemplate transactionTemplate;
   @NotNull
   private SQLFactory sqlFactory;
   @NotEmpty
   private String tableName = "doc_store";
   @NotEmpty
   private String idColumnName = "id";
   @NotEmpty
   private String updColumnName = "updated";
   private int idMaxLength = 256;
   private DocStateHolder docStateHolder;

   public StateDocumentProducer(DocumentProducer producer) {
      if (producer == null) {
         throw new NullPointerException("producer cannot be null");
      }
      this.producer = producer;
   }

   @Override
   public void init() {
      try {
         BeanValidator.validate(this);
      } catch (PipelineException e) {
         throw new RuntimeException(e);
      }
      producer.init();

      if (docStateHolder == null) {
         docStateHolder = new DocStateHolder(jdbcTemplate, sqlFactory, tableName, idColumnName, idMaxLength,
               updColumnName);
      }
      docStateHolder.setTransactionTemplate(transactionTemplate);
      docStateHolder.prepare();
   }

   @Override
   public void close() {
      producer.close();
      docStateHolder.commit();
   }

   @Override
   public void fail() {
      producer.fail();
      docStateHolder.rollback();
   }

   @Override
   public Iterator<Document> iterator() {
      return new StateDocumentIterator(producer.iterator(), docStateHolder, idFieldName);
   }

   public String getIdFieldName() {
      return idFieldName;
   }

   public void setIdFieldName(String idFieldName) {
      this.idFieldName = idFieldName;
   }

   public String getTableName() {
      return tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public String getIdColumnName() {
      return idColumnName;
   }

   public void setIdColumnName(String idColumnName) {
      this.idColumnName = idColumnName;
   }

   public String getUpdColumnName() {
      return updColumnName;
   }

   public void setUpdColumnName(String updColumnName) {
      this.updColumnName = updColumnName;
   }

   public int getIdMaxLength() {
      return idMaxLength;
   }

   public void setIdMaxLength(int idMaxLength) {
      this.idMaxLength = idMaxLength;
   }

   public SimpleJdbcTemplate getJdbcTemplate() {
      return jdbcTemplate;
   }

   public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
   }

   public TransactionTemplate getTransactionTemplate() {
      return transactionTemplate;
   }

   public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
      this.transactionTemplate = transactionTemplate;
   }

   public SQLFactory getSqlFactory() {
      return sqlFactory;
   }

   public void setSqlFactory(SQLFactory sqlFactory) {
      this.sqlFactory = sqlFactory;
   }

   private static class StateDocumentIterator implements Iterator<Document> {
      private final Iterator<Document> iterator;
      private final DocStateHolder docStateHolder;
      private Iterator<Document> delIterator;
      private String idFieldName;

      public StateDocumentIterator(Iterator<Document> iterator, DocStateHolder docStateHolder, String idFieldName) {
         this.iterator = iterator;
         this.docStateHolder = docStateHolder;
         this.idFieldName = idFieldName;
      }

      @Override
      public boolean hasNext() {
         if (iterator.hasNext()) {
            return true;
         }
         if (delIterator == null) {
            delIterator = createDelIterator();
         }
         return delIterator.hasNext();
      }

      private Iterator<Document> createDelIterator() {
         return new IdDocumentIterator(docStateHolder.findDeletedIds(), idFieldName);
      }

      @Override
      public Document next() {
         if (!hasNext()) {
            throw new NoSuchElementException();
         }
         if (delIterator == null) {
            final Document doc = iterator.next();
            if (docStateHolder.isUpdate(doc.getFieldValue(idFieldName))) {
               doc.setOperation(DocumentOperation.MODIFY_VALUE);
            } else {
               doc.setOperation(DocumentOperation.ADD_VALUE);
            }
            return doc;
         }
         return delIterator.next();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

   }

   private static class IdDocumentIterator implements Iterator<Document> {
      private final Iterator<String> it;
      private final String idFieldName;

      public IdDocumentIterator(Iterator<String> it, String idFieldName) {
         this.it = it;
         this.idFieldName = idFieldName;
      }

      @Override
      public boolean hasNext() {
         return it.hasNext();
      }

      @Override
      public Document next() {
         final Document doc = new Document();
         doc.setOperation(DocumentOperation.DELETE_VALUE);
         doc.setFieldValue(idFieldName, it.next());
         return doc;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
