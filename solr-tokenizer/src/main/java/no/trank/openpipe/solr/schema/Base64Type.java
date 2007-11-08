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
package no.trank.openpipe.solr.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.zip.InflaterInputStream;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.AbstractField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.solr.core.SolrException;
import static org.apache.solr.core.SolrException.ErrorCode.SERVER_ERROR;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TextField;

import no.trank.openpipe.solr.analysis.BinaryIO;
import no.trank.openpipe.solr.analysis.BinaryTokenDeserializer;
import no.trank.openpipe.solr.analysis.io.Base64InputStream;
import no.trank.openpipe.solr.util.IOUtil;

/**
 * A field type for pre-tokenized field-values stored on a binary base64-encoded form.
 * <br/>
 * Uses {@link Base64InputStream} for decoding the base64-encoded string.
 * <br/>
 * Uses {@link BinaryIO} to verify version of serialized data and to find compression settings for the data.
 * <p>
 * Reading a field-value, {@link #createField(SchemaField, String, float)}, does the following:
 * <pre>
 * InputStream in = new Base64InputStream(externalVal);
 * if (BinaryIO.readHeaderIsCompressed(in)) {
 *    in = new InflaterInputStream(in);
 * }
 * String untokenizedValue = IOUtil.readUTF(in);
 * </pre>
 * The tokens are parsed as follows:
 * <pre>
 * Fieldable {
 *    ...
 *    public TokenStream tokenStreamValue() {
 *       return new BinaryTokenDeserializer(in);
 *    }
 * </pre>
 * Where <tt>in</tt> is the stream openend in {@link #createField(SchemaField, String, float)}.
 * <p/>
 *
 * @see IOUtil#readUTF(InputStream)
 * @see BinaryTokenDeserializer
 *
 * @version $Revision$
 */
public class Base64Type extends TextField {

   /**
    * Creates a field from a pre-tokenized field from a binary base64-encoded string.
    * 
    * @param field the field info as read from schema.
    * @param externalVal the base64-encoded string.
    * @param boost the boost of this field.
    * 
    * @return a <tt>Fieldable</tt> as read from <tt>externalVal</tt> described {@linkplain Base64Type here}.
    */
   @Override
   public Fieldable createField(SchemaField field, String externalVal, float boost) {
      if (externalVal == null) {
         return null;
      }
      if (!field.indexed() && !field.stored()) {
         log.finest("Ignoring unindexed/unstored field: " + field);
         return null;
      }
      InputStream in = new Base64InputStream(externalVal);
      try {
         if (BinaryIO.readHeaderIsCompressed(in)) {
            in = new InflaterInputStream(in);
         }
         final String val = IOUtil.readUTF(in);
         final Fieldable f = new Base64Field(field.getName(), val, getFieldStore(field, val), getFieldIndex(field, val),
               getFieldTermVec(field, val), in);
         f.setOmitNorms(field.omitNorms());
         f.setBoost(boost);
         return f;
      } catch (IOException e) {
         throw new SolrException(SERVER_ERROR, "Could not create field '" + field + "' from value '" + externalVal + 
               "'", e, false);
      }
   }

   private static class Base64Field extends AbstractField {
      private final String val;
      private final InputStream in;

      private Base64Field(String name, String val, Field.Store store, Field.Index index, Field.TermVector termVector, 
            InputStream in) {
         super(name, store, index, termVector);
         this.val = val;
         this.in = in;
      }

      @Override
      public String stringValue() {
         return val;
      }

      @Override
      public Reader readerValue() {
         return BinaryTokenDeserializer.createDummyReader(in);
      }

      @Override
      public byte[] binaryValue() {
         return null;
      }
      
      @Override
      public TokenStream tokenStreamValue() {
         return new BinaryTokenDeserializer(in);
      }
   }
}
