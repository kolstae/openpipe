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
import javax.sql.DataSource;

import org.apache.ws.jaxme.sqls.SQLFactory;

import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;
import no.trank.openpipe.api.document.DocumentProducer;
import no.trank.openpipe.config.BeanValidator;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;

/**
 * A &quot;decorator&quot; document producer, that tracks whether a document is new, updated or deleted using
 * {@link IdStateHolder}.<br/>
 * <b>Note:</b> Documents produced by {@link #getProducer()} must contain a field with a field value for a
 * field with name {@link #getIdFieldName()}.
 *
 * @version $Revision$
 */
public class StateDocumentProducer implements DocumentProducer, TableDescription {
   private final DocumentProducer producer;
   @NotEmpty
   private String idFieldName;
   @NotNull
   private SQLFactory sqlFactory;
   @NotNull
   private DataSource dataSource;
   @NotEmpty
   private String tableName = "doc_store";
   @NotEmpty
   private String idColumnName = "id";
   @NotEmpty
   private String updColumnName = "updated";
   private int idMaxLength = 256;
   private IdStateHolder idStateHolder;

   /**
    * Creates a document producer that decorates <tt>producer</tt>.
    *
    * @param producer the producer to decorate.
    */
   public StateDocumentProducer(DocumentProducer producer) {
      if (producer == null) {
         throw new NullPointerException("producer cannot be null");
      }
      this.producer = producer;
   }

   /**
    * Calls {@link #getProducer()}<tt>.prapare()</tt> and then {@link IdStateHolder#prepare()}.
    */
   @Override
   public void init() {
      try {
         BeanValidator.validate(this);
      } catch (PipelineException e) {
         throw new RuntimeException(e);
      }
      producer.init();

      if (idStateHolder == null) {
         idStateHolder = new IdStateHolder(dataSource, sqlFactory, this);
      }
      idStateHolder.prepare();
   }

   /**
    * Calls {@link #getProducer()}<tt>.close()</tt> and then {@link IdStateHolder#commit()}.
    */
   @Override
   public void close() {
      producer.close();
      if (idStateHolder != null) {
         idStateHolder.commit();
      }
   }

   /**
    * Calls {@link #getProducer()}<tt>.fail()</tt> and then {@link IdStateHolder#rollback()}.
    */
   @Override
   public void fail() {
      producer.fail();
      if (idStateHolder != null) {
         idStateHolder.rollback();
      }
   }

   /**
    * Returns an iterator that first updates {@link Document#setOperation(String)} of all documents returned by
    * {@link #getProducer()}<tt>.iterator()</tt> to either {@link DocumentOperation#ADD_VALUE} or
    * {@link DocumentOperation#MODIFY_VALUE} based on the outcome of
    * {@link IdStateHolder#isUpdate(String)}. And then documents with id in
    * {@link IdStateHolder#findDeletedIds()} and {@link Document#setOperation(String) Doument.setOperation(}
    * {@link DocumentOperation#DELETE_VALUE}<tt>)</tt>.
    */
   @Override
   public Iterator<Document> iterator() {
      return new StateDocumentIterator(producer.iterator(), idStateHolder, idFieldName);
   }

   public DocumentProducer getProducer() {
      return producer;
   }

   /**
    * Get in what field of the document the id can be found.
    *
    * @return the name of the id-field.
    */
   public String getIdFieldName() {
      return idFieldName;
   }

   /**
    * Set in what field of the document the id can be found.
    *
    * @param idFieldName the name of the id-field. <em>Cannot</em> be <tt>null</tt> or <tt>&quot;&quot;</tt>.
    */
   public void setIdFieldName(String idFieldName) {
      this.idFieldName = idFieldName;
   }

   /**
    * {@inheritDoc}
    * Default value is <tt>&quot;doc_store&quot;</tt>.
    */
   @Override
   public String getTableName() {
      return tableName;
   }

   /**
    * Sets the name of the table.
    *
    * @param tableName the name of the table. <em>Cannot</em> be <tt>null</tt> or <tt>&quot;&quot;</tt>.
    */
   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   /**
    * {@inheritDoc}
    * Default value is <tt>&quot;id&quot;</tt>.
    */
   @Override
   public String getIdColumnName() {
      return idColumnName;
   }

   /**
    * Sets the name of the <tt>ID</tt> column.
    *
    * @param idColumnName the name of the <tt>ID</tt> column. <em>Cannot</em> be <tt>null</tt> or <tt>&quot;&quot;</tt>.
    */
   public void setIdColumnName(String idColumnName) {
      this.idColumnName = idColumnName;
   }

   /**
    * {@inheritDoc}
    * Default value is <tt>&quot;updated&quot;</tt>.
    */
   @Override
   public String getUpdColumnName() {
      return updColumnName;
   }

   /**
    * Sets the name of the <tt>lastUpdated</tt> column.
    *
    * @param updColumnName the name of the <tt>lastUpdated</tt> column. <em>Cannot</em> be <tt>null</tt> or
    * <tt>&quot;&quot;</tt>.
    */
   public void setUpdColumnName(String updColumnName) {
      this.updColumnName = updColumnName;
   }

   /**
    * {@inheritDoc}
    * Default value is <tt>256</tt>.
    */
   @Override
   public int getIdMaxLength() {
      return idMaxLength;
   }

   public void setIdMaxLength(int idMaxLength) {
      this.idMaxLength = idMaxLength;
   }

   /**
    * Gets the sql factory used to create the SQL needed.
    *
    * @return the sql factory used to create the SQL needed.
    */
   public SQLFactory getSqlFactory() {
      return sqlFactory;
   }

   /**
    * Sets the sql factory used to create the SQL needed.
    *
    * @param sqlFactory the sql factory used to create the SQL needed. <em>Cannot</em> be <tt>null</tt>.
    */
   public void setSqlFactory(SQLFactory sqlFactory) {
      this.sqlFactory = sqlFactory;
   }

   /**
    * Gets the datasource used for tracking document ids.
    *
    * @return the datasource used for tracking document ids.
    */
   public DataSource getDataSource() {
      return dataSource;
   }

   /**
    * Sets the datasource used for tracking document ids.
    *
    * @param dataSource the datasource used for tracking document ids. <em>Cannot</em> be <tt>null</tt>.
    */
   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
   }

   private static class StateDocumentIterator implements Iterator<Document> {
      private final Iterator<Document> iterator;
      private final IdStateHolder idStateHolder;
      private Iterator<Document> delIterator;
      private String idFieldName;

      public StateDocumentIterator(Iterator<Document> iterator, IdStateHolder idStateHolder, String idFieldName) {
         this.iterator = iterator;
         this.idStateHolder = idStateHolder;
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
         return new IdDocumentIterator(idStateHolder.findDeletedIds(), idFieldName);
      }

      @Override
      public Document next() {
         if (!hasNext()) {
            throw new NoSuchElementException();
         }
         if (delIterator == null) {
            final Document doc = iterator.next();
            if (idStateHolder.isUpdate(doc.getFieldValue(idFieldName))) {
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
