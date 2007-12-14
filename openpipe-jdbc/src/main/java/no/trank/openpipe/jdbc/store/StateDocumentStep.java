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
import javax.sql.DataSource;

import org.apache.ws.jaxme.sqls.SQLFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.BaseSubPipeline;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.SubPipeline;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.api.document.DocumentOperation;
import no.trank.openpipe.config.annotation.NotEmpty;
import no.trank.openpipe.config.annotation.NotNull;

/**
 * A pipeline step that tracks whether a document is new, updated or deleted using {@link IdStateHolder}.
 *
 * @version $Revision$
 */
public class StateDocumentStep extends BasePipelineStep implements TableDescription {
   private IdStateHolder idStateHolder;
   @NotNull
   private DataSource dataSource;
   @NotNull
   private SQLFactory sqlFactory;
   @NotEmpty
   private String tableName = "doc_store";
   @NotEmpty
   private String idColumnName = "id";
   @NotEmpty
   private String updColumnName = "updated";
   private int idMaxLength = 256;
   @NotNull
   private SubPipeline deletePipeline = new BaseSubPipeline();
   @NotEmpty
   private String idFieldName;
   private boolean lifecycleSubPipeline = true;

   /**
    * Creates a step with the name &quot;StateDocumentStep&quot;.
    */
   public StateDocumentStep() {
      super("StateDocumentStep");
   }

   /**
    * Sets {@link Document#setOperation(String)} to either {@link DocumentOperation#ADD_VALUE} or
    * {@link DocumentOperation#MODIFY_VALUE} based on the outcome of
    * {@link IdStateHolder#isUpdate(String) idStateHolder.isUpdate(id)}.
    *
    * @param doc the document to check.
    *
    * @return {@link PipelineStepStatus#DEFAULT}.
    *
    * @throws PipelineException if {@link Document#getFieldValue(String) doc.getFieldValue(idFieldName)}
    * <tt>== null</tt>.
    */
   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      final String id = doc.getFieldValue(idFieldName);
      if (id == null) {
         throw new PipelineException("idFieldName '" + idFieldName + "' is null");
      }
      if (idStateHolder.isUpdate(id)) {
         doc.setOperation(DocumentOperation.MODIFY_VALUE);
      } else {
         doc.setOperation(DocumentOperation.ADD_VALUE);
      }
      return PipelineStepStatus.DEFAULT;
   }

   @Override
   public void prepare() throws PipelineException {
      super.prepare();

      if (lifecycleSubPipeline) {
         deletePipeline.prepare();
      }

      if (idStateHolder == null) {
         idStateHolder = new IdStateHolder(dataSource, sqlFactory, this);
      }
      idStateHolder.prepare();
   }

   /**
    * If <tt>success == true</tt> {@link #getDeletePipeline()}<tt>.execute()</tt> will be called for each document id
    * that found to be deleted and finally {@link IdStateHolder#commit()}. <br/>
    * {@link IdStateHolder#rollback()} is called if <tt>success == false</tt>. <br/>
    * {@link #getDeletePipeline() deletePipeline}<tt>.finish(success)</tt> is called if
    * {@link #isLifecycleSubPipeline()} <tt>== true</tt>.
    *
    * @param success {@inheritDoc}
    * 
    * @see IdStateHolder#findDeletedIds()
    */
   @Override
   public void finish(boolean success) throws PipelineException {
      if (idStateHolder != null) {
         if (success) {
            executeDeletePipeline();
            idStateHolder.commit();
         } else {
            idStateHolder.rollback();
         }
      }

      if (lifecycleSubPipeline) {
         deletePipeline.finish(success);
      }
   }

   private void executeDeletePipeline() throws PipelineException {
      final Iterator<String> it = idStateHolder.findDeletedIds();
      while (it.hasNext()) {
         final Document doc = new Document();
         doc.setFieldValue(idFieldName, it.next());
         doc.setOperation(DocumentOperation.DELETE_VALUE);
         deletePipeline.executeSteps(doc);
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
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
    * Gets the pipeline to execute for deleted documents.
    *
    * @return the pipeline to execute for deleted documents.
    */
   public SubPipeline getDeletePipeline() {
      return deletePipeline;
   }

   /**
    * Sets the pipeline to execute for deleted documents.
    *
    * @param deletePipeline the pipeline to execute for deleted documents. <em>Cannot</em> be <tt>null</tt>.
    */
   public void setDeletePipeline(SubPipeline deletePipeline) {
      this.deletePipeline = deletePipeline;
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
    * Gets whether to call <tt>prepare()</tt> and <tt>finish()</tt> on {@link #getDeletePipeline() deletePipeline}.
    * Default value is <tt>true</tt>.
    *
    * @return <tt>true</tt> if <tt>prepare()</tt> and <tt>finish()</tt> should be called on
    * {@link #getDeletePipeline() deletePipeline}.
    */
   public boolean isLifecycleSubPipeline() {
      return lifecycleSubPipeline;
   }

   /**
    * Sets whether to call <tt>prepare()</tt> and <tt>finish()</tt> on {@link #getDeletePipeline() deletePipeline}.
    *
    * @param lifecycleSubPipeline set to <tt>true</tt> if <tt>prepare()</tt> and <tt>finish()</tt> should be called on
    * {@link #getDeletePipeline() deletePipeline}.
    *
    * @see #isLifecycleSubPipeline()
    */
   public void setLifecycleSubPipeline(boolean lifecycleSubPipeline) {
      this.lifecycleSubPipeline = lifecycleSubPipeline;
   }
}
