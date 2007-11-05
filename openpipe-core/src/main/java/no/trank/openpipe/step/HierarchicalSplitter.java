package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.Document;

/**
 * @version $Revision$
 */
public class HierarchicalSplitter extends BasePipelineStep {
   private static Logger log = LoggerFactory.getLogger(HierarchicalSplitter.class);
   private String fromFieldName;
   private String toFieldName;
   private int numLevels;
   private String levelSplit;
   private String alternativeSplit;

   public HierarchicalSplitter() {
      super("HierarchicalSplitter");
   }

   @Override
   public PipelineStepStatus execute(Document doc) throws PipelineException {
      String text = doc.getFieldValue(fromFieldName);
      if(text != null && text.length() > 0) {
         List<String> values = resolveSplits(text);
         if(values.isEmpty()) {
            doc.removeField(toFieldName);
         }
         else {
            doc.setFieldValues(toFieldName, values);
         }
      }
      else {
         log.debug("Missing field '{}'", fromFieldName);
         doc.removeField(toFieldName);
      }
      
      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   private List<String> resolveSplits(String text) {
      String[] levels = text.split(levelSplit);
      List<String> ret = new ArrayList<String>();
      
      int lastLevel = Math.min(numLevels > 0 ? numLevels : Integer.MAX_VALUE, levels.length); 
      
      for(int size = 1; size <= lastLevel; ++size) {
         int[] ind = new int[size];
         
         boolean incr = false;
         while(!incr) {
            String cat = "";
            incr = true;
            for(int i = size-1; i >= 0; --i) {
               String[] tmp = levels[i].split(alternativeSplit);
               cat = (i > 0 ? levelSplit : "") + tmp[ind[i]] + cat;
               if(incr) {
                  ind[i] = (ind[i] + 1) % tmp.length;
                  incr = ind[i] == 0;
               }
            }
            
            ret.add(cat);
         }
      }
      
      if(log.isDebugEnabled()) {
         log.debug("Resolved " + ret.size() + " split" + (ret.size() == 1 ? "" : "s") +
                   " over " + lastLevel + " level" + (lastLevel == 1 ? "" : "s"));
      }
      
      return ret;
   }
   
   @Override
   public String getRevision() {
      return "$Revision$";
   }

   public String getAlternativeSplit() {
      return alternativeSplit;
   }

   public void setAlternativeSplit(String alternativeSplit) {
      this.alternativeSplit = alternativeSplit;
   }

   public String getFromFieldName() {
      return fromFieldName;
   }

   public void setFromFieldName(String fromFieldName) {
      this.fromFieldName = fromFieldName;
   }

   public String getLevelSplit() {
      return levelSplit;
   }

   public void setLevelSplit(String levelSplit) {
      this.levelSplit = levelSplit;
   }

   public int getNumLevels() {
      return numLevels;
   }

   public void setNumLevels(int numLevels) {
      this.numLevels = numLevels;
   }

   public String getToFieldName() {
      return toFieldName;
   }

   public void setToFieldName(String toFieldName) {
      this.toFieldName = toFieldName;
   }
}