package no.trank.openpipe.step;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;
import static no.trank.openpipe.util.HexUtil.toHexString;

/**
 * @version $Revision$
 */
public class ChecksumFields extends BasePipelineStep {
   private List<String> fieldNames;
   private String outField;
   private String algorithm = "MD5";
   private MessageDigest messageDigest;

   public ChecksumFields() {
      super("ChecksumFields");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      try {
         messageDigest.reset();
         for (String fieldName : fieldNames) {
            final List<String> values = doc.getFieldValues(fieldName);
            for (String value : values) {
               messageDigest.update(value.getBytes("UTF-8"));
            }
         }
         doc.setFieldValue(outField, toHexString(messageDigest.digest()));
      } catch (UnsupportedEncodingException e) {
         throw new PipelineException(e);
      }
      return PipelineStepStatus.DEFAULT;
   }

   public List<String> getFieldNames() {
      return fieldNames;
   }

   public void setFieldNames(List<String> fieldNames) {
      this.fieldNames = fieldNames;
   }

   public String getOutField() {
      return outField;
   }

   public void setOutField(String outField) {
      this.outField = outField;
   }

   public String getAlgorithm() {
      return algorithm;
   }

   public void setAlgorithm(String algorithm) {
      this.algorithm = algorithm;
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   @Override
   public void prepare() throws PipelineException {
      if (fieldNames == null || fieldNames.isEmpty()) {
         throw new PipelineException("No field-names configured");
      }
      try {
         messageDigest = MessageDigest.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
         throw new PipelineException(e);
      }
   }

   @Override
   public void finish(boolean success) {
      messageDigest = null;
   }
}