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
package no.trank.openpipe.api.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version $Revision$
 */
public class Document {
   private static final Logger log = LoggerFactory.getLogger(Document.class);
   private final Map<String, ArrayList<AnnotatedField>> fieldMap = new LinkedHashMap<String, ArrayList<AnnotatedField>>();
   private RawData rawData;
   private String operation;

   /**
    * Constructs a document without raw-data. Equivalent of <tt>new Document(null)</tt>.
    * 
    * @see #Document(RawData)
    */
   public Document() {
      this(null);
   }

   /**
    * Constructs a document with the given raw-data.
    * 
    * @param rawData the raw-data of this document, can be <tt>null</tt>.
    * 
    * @see #Document()
    */
   public Document(RawData rawData) {
      this.rawData = rawData;
   }

   /**
    * Gets a set of all field-names of this document.
    * 
    * @return an unmodifiable set of field-names. This method <i>never</i> returns <tt>null</tt>.
    */
   public Set<String> getFieldNames() {
      return Collections.unmodifiableSet(fieldMap.keySet());
   }

   /**
    * Gets the field-value, as a string, of a given field. <b>Note</b>: if wanted field has more than one value, only 
    * the first value is returned, and a warning is logged. 
    * 
    * @param fieldName the name of the wanted field.
    * 
    * @return the value of the field or <tt>null</tt>.
    */
   public String getFieldValue(String fieldName) {
      final AnnotatedField field = getField(fieldName);
      return field == null ? null : field.getValue();
   }

   /**
    * Gets the field-value, of a given field. <b>Note</b>: if wanted field has more than one value, only the first value 
    * is returned, and a warning is logged. 
    * 
    * @param fieldName the name of the wanted field.
    * 
    * @return the value of the field or <tt>null</tt>.
    */
   public AnnotatedField getField(String fieldName) {
      final ArrayList<AnnotatedField> fieldList = fieldMap.get(fieldName);
      if (fieldList != null && !fieldList.isEmpty()) {
         if (fieldList.size() > 1) {
            log.warn("getField({}) called on multivalued field.", fieldName);
         }
         return fieldList.get(0);
      }
      return null;
   }

   /**
    * Gets the field-values, as strings, of a given field.
    * 
    * @param fieldName the name of the wanted field.
    * 
    * @return an unmodifiable list of strings. This method <i>never</i> returns <tt>null</tt>.
    */
   public List<String> getFieldValues(String fieldName) {
      final List<AnnotatedField> fieldList = fieldMap.get(fieldName);
      if (fieldList != null) {
         final List<String> fieldValueList = new ArrayList<String>(fieldList.size());
         for (AnnotatedField annotatedField : fieldList) {
            fieldValueList.add(annotatedField.getValue());
         }
         return Collections.unmodifiableList(fieldValueList);
      }
      return Collections.emptyList();
   }

   /**
    * Gets the field-values of a given field.
    * 
    * @param fieldName the name of the wanted field.
    * 
    * @return an unmodifiable list of field-values. This method <i>never</i> returns <tt>null</tt>.
    */
   public List<AnnotatedField> getFields(String fieldName) {
      final List<AnnotatedField> fieldList = fieldMap.get(fieldName);
      if (fieldList != null) {
         return Collections.unmodifiableList(fieldList);
      }
      return Collections.emptyList();
   }

   /**
    * Sets field-values to a given field. If either <tt>name</tt> or <tt>fields</tt> is <tt>null</tt> no change will 
    * occur.
    * 
    * @param name the name of the field.
    * @param fieldList the new values of the field.
    */
   public void setField(String name, List<? extends AnnotatedField> fieldList) {
      if (notNull(name, fieldList)) {
         if (fieldList instanceof ArrayList) {
            @SuppressWarnings("unchecked")
            final ArrayList<AnnotatedField> list = (ArrayList<AnnotatedField>) fieldList;
            fieldMap.put(name, list);
         } else {
            fieldMap.put(name, new ArrayList<AnnotatedField>(fieldList));         
         }
      }
   }

   /**
    * Sets field-value to a given field. If either <tt>name</tt> or <tt>field</tt> is <tt>null</tt> no change will 
    * occur.
    * 
    * @param name the name of the field.
    * @param field the new value of the field.
    */
   public void setField(String name, AnnotatedField field) {
      if (notNull(name, field)) {
         final ArrayList<AnnotatedField> list = new ArrayList<AnnotatedField>();
         list.add(field);
         fieldMap.put(name, list);
      }
   }

   /**
    * Sets field-value to a given field. If either <tt>name</tt> or <tt>value</tt> is <tt>null</tt> no change will 
    * occur.
    * 
    * @param name the name of the field.
    * @param value the new value of the field.
    */
   public void setFieldValue(String name, String value) {
      if (notNull(name, value)) {
         setField(name, new BaseAnnotatedField(value));
      }
   }
   
   /**
    * Sets field-values to a given field. If either <tt>name</tt> or <tt>values</tt> is <tt>null</tt> no change will 
    * occur.
    * 
    * @param name the name of the field.
    * @param values the new values of the field.
    */
   public void setFieldValues(String name, List<String> values) {
      if (notNull(name, values)) {
         final ArrayList<AnnotatedField> fieldList = new ArrayList<AnnotatedField>();
         for (String value : values) {
            fieldList.add(new BaseAnnotatedField(value));
         }
         fieldMap.put(name, fieldList);
      }
   }

   /**
    * Adds field-value to a given field. If either <tt>name</tt> or <tt>field</tt> is <tt>null</tt> no change will 
    * occur.
    * 
    * @param name the name of the field.
    * @param field the value to be added to the field.
    */
   public void addField(String name, AnnotatedField field) {
      if (notNull(name, field)) {
         ArrayList<AnnotatedField> fieldList = fieldMap.get(name);
         if (fieldList == null) {
            fieldList = new ArrayList<AnnotatedField>();
            fieldMap.put(name, fieldList);
         }
         fieldList.add(field);
      }
   }

   /**
    * Adds field-values to a given field. If either <tt>name</tt> or <tt>fields</tt> is <tt>null</tt> no change will 
    * occur.
    * 
    * @param name the name of the field.
    * @param fields the values to be added to the field.
    */
   public void addField(String name, List<? extends AnnotatedField> fields) {
      if (notNull(name, fields)) {
         final List<AnnotatedField> fieldList = fieldMap.get(name);
         if (fieldList == null) {
            setField(name, fields);
         } else {
            fieldList.addAll(fields);
         }
      }
   }

   /**
    * Adds field-value to a given field. If either <tt>name</tt> or <tt>value</tt> is <tt>null</tt> no change will 
    * occur.
    * 
    * @param name the name of the field.
    * @param value the value to be added to the field.
    */
   public void addFieldValue(String name, String value) {
      if (notNull(name, value)) {
         addField(name, new BaseAnnotatedField(value));
      }
   }

   /**
    * Adds field-values to a given field. If either <tt>name</tt> or <tt>values</tt> is <tt>null</tt> no change will 
    * occur.
    * 
    * @param name the name of the field.
    * @param values the values to be added the field.
    */
   public void addFieldValues(String name, List<String> values) {
      if (notNull(name, values)) {
         ArrayList<AnnotatedField> fieldList = fieldMap.get(name);
         if (fieldList == null) {
            fieldList = new ArrayList<AnnotatedField>(values.size());
         }
         for (String value : values) {
            fieldList.add(new BaseAnnotatedField(value));
         }
      }
   }

   /**
    * Removes a field from the document.
    * 
    * @param name the field to remove.
    * 
    * @return <tt>true</tt> if field with name <tt>name</tt> was removed.
    */
   public boolean removeField(String name) {
      return fieldMap.remove(name) != null;
   }

   /**
    * Returns <tt>true</tt> if this document contains a field with name <tt>name</tt>.
    * 
    * @param name the name of the field.
    * 
    * @return <tt>true</tt> if this document contains a field with name <tt>name</tt>.
    */
   public boolean containsField(String name) {
      return fieldMap.containsKey(name);
   }

   /**
    * Gets the raw-data of this document if any.
    * 
    * @return the raw-data or <tt>null</tt>.
    */
   public RawData getRawData() {
      return rawData;
   }

   /**
    * Sets the raw-data of this document.
    *
    * @param rawData the raw-data. 
    */
   public void setRawData(RawData rawData) {
      this.rawData = rawData;
   }

   /**
    * Gets the operation of this document if any.
    * 
    * @return the operation or <tt>null</tt>.
    */
   public String getOperation() {
      return operation;
   }

   /**
    * Sets the operation of this document.
    * 
    * @param operation the new operation of this document.
    */
   public void setOperation(String operation) {
      this.operation = operation;
   }

   /**
    * A sane (but somwhat heavy-weight) string representation of this object.
    * 
    * @return a string representation of this object.
    */
   @Override
   public String toString() {
      return "Document{" +
            "fieldMap=" + fieldMap +
            ", operation=" + operation +
            '}';
   }

   private static boolean notNull(String name, Object value) {
      return name != null && value != null;
   }
}
