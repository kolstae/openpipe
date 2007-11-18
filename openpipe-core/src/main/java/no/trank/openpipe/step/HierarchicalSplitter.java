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
package no.trank.openpipe.step;

import java.util.ArrayList;
import java.util.List;

import no.trank.openpipe.api.MultiInputOutputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This step splits input text according to a few simple rules and outputs the combinations to a multi valued field:
 * 
 * <p><ul>
 *    <li>The property <code>levelSplit</code> is used to split the text into separate levels.</li>
 *    <li>The property <code>alternativeSplit</code> is used to split the text on a level into alternatives.</li>
 *    <li>The property <code>numLevels</code> is used to stop the output after a set number of levels.</li>
 * </ul>
 *  
 * 
 * <p><h4>Examples</h4>
 * 
 * <table>
 *    <tr>
 *       <td><code>levelSplit</code>:</td>
 *       <td>/</td>
 *    </tr>
 *    <tr>
 *       <td><code>alternativeSplit</code>:</td>
 *       <td>¦</td>
 *    </tr>
 *    <tr>
 *       <td><code>numLevels</code>:</td>
 *       <td>0 (no restriction)</td>
 *    </tr>
 *    <tr>
 *       <td>Input:</td>
 *       <td>a/b¦c/d</td>
 *    </tr>
 *    <tr>
 *       <td valign="top">Output:</td>
 *       <td>
 *          <ul>
 *             <li>a</li>
 *             <li>a/b</li>
 *             <li>a/c</li>
 *             <li>a/b/d</li>
 *             <li>a/c/d</li>
 *          </ul>
 *       </td>
 *    </tr>
 * </table>
 *
 *
 * <p><table>
 *    <tr>
 *       <td><code>levelSplit</code>:</td>
 *       <td>/</td>
 *    </tr>
 *    <tr>
 *       <td><code>alternativeSplit</code>:</td>
 *       <td>¦</td>
 *    </tr>
 *    <tr>
 *       <td><code>numLevels</code>:</td>
 *       <td>2</td>
 *    </tr>
 *    <tr>
 *       <td>Input:</td>
 *       <td>a/b¦c/d</td>
 *    </tr>
 *    <tr>
 *       <td valign="top">Output:</td>
 *       <td>
 *          <ul>
 *             <li>a</li>
 *             <li>a/b</li>
 *             <li>a/c</li>
 *          </ul>
 *       </td>
 *    </tr>
 * </table>
 * 
 * @version $Revision$
 */
public class HierarchicalSplitter extends MultiInputOutputFieldPipelineStep {
   private static Logger log = LoggerFactory.getLogger(HierarchicalSplitter.class);
   private int numLevels;
   @NotNull
   private String levelSplit;
   @NotNull
   private String alternativeSplit;

   public HierarchicalSplitter() {
      super("HierarchicalSplitter", false);
   }

   @Override
   protected void process(Document doc, String inputFieldName, List<AnnotatedField> inputFields, String outputFieldName)
         throws PipelineException {
      if(inputFields.isEmpty()) {
         log.debug("Missing field '{}'", inputFieldName);
         doc.removeField(outputFieldName);
      }
      else {
         List<String> outValues = new ArrayList<String>();
         for(AnnotatedField field : inputFields) {
            String text = field.getValue();
            if(text != null && text.length() > 0) {
               List<String> values = resolveSplits(text);
               if(!values.isEmpty()) {
                  outValues.addAll(values);
               }
            }
         }

         if (outValues.isEmpty()) {
            doc.removeField(outputFieldName);
         } else {
            doc.setFieldValues(outputFieldName, outValues);
         }
      }
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

   /**
    * Gets the split used to separate the alternatives within a level of the input.
    * 
    * @return the split
    */
   public String getAlternativeSplit() {
      return alternativeSplit;
   }

   /**
    * Sets the split used to separate the alternatives within a level of the input.
    * 
    * @param alternativeSplit the split
    */
   public void setAlternativeSplit(String alternativeSplit) {
      this.alternativeSplit = alternativeSplit;
   }

   /**
    * Gets the split used to separate the levels in the input.
    * 
    * @return the split
    */
   public String getLevelSplit() {
      return levelSplit;
   }

   /**
    * Sets the split used to separate the levels in the input.
    * 
    * @param levelSplit
    */
   public void setLevelSplit(String levelSplit) {
      this.levelSplit = levelSplit;
   }

   /**
    * Gets the max number of levels in the output.
    * 
    * @return the number of levels
    */
   public int getNumLevels() {
      return numLevels;
   }

   /**
    * Sets the max number of levels in the output. There is no restriction if not set. 
    * 
    * @param numLevels the number of levels
    */
   public void setNumLevels(int numLevels) {
      this.numLevels = numLevels;
   }
}