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

import no.trank.openpipe.solr.analysis.Base64IO;
import no.trank.openpipe.solr.analysis.Base64TokenDeserializer;
import no.trank.openpipe.solr.analysis.io.Base64InputStream;
import no.trank.openpipe.solr.util.IOUtil;

/**
 * @version $Revision$
 */
public class Base64Type extends TextField {
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
         if (Base64IO.readHeaderIsCompressed(in)) {
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

      public String stringValue() {
         return val;
      }

      public Reader readerValue() {
         return Base64TokenDeserializer.createDummyReader(in);
      }

      public byte[] binaryValue() {
         return null;
      }
      
      public TokenStream tokenStreamValue() {
         return new Base64TokenDeserializer(in);
      }
   }
}
