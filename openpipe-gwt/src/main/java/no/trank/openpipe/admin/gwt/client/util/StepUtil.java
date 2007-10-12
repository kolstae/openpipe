package no.trank.openpipe.admin.gwt.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.trank.openpipe.admin.gwt.client.model.StepConfig;
import no.trank.openpipe.admin.gwt.client.model.StepValues;
import no.trank.openpipe.admin.gwt.client.model.Subpipeline;
import no.trank.openpipe.admin.gwt.client.model.type.FieldType;
import no.trank.openpipe.admin.gwt.client.model.type.ListType;
import no.trank.openpipe.admin.gwt.client.model.type.MapType;
import no.trank.openpipe.admin.gwt.client.model.type.StepType;

/**
 * @version $Revision: 874 $
 */
public class StepUtil {
   public static String getName(StepConfig stepConfig) {
      String ret = (String)stepConfig.getValues().get("name");
      ret = ret == null ? "" : ret.trim();
      return ret.length() > 0 ? ret : stepConfig.getClassName(); 
   }
   
   public static Subpipeline[] getSubpipelines(List steps) {
      List ret = new ArrayList();
      for(Iterator it = steps.iterator(); it.hasNext(); ) {
         StepValues step = (StepValues)it.next();
         for(Iterator mapit = step.getStepConfig().getFieldMap().entrySet().iterator(); mapit.hasNext(); ) {
            Entry entry = (Entry)mapit.next();
            String prefix = step.getValue("name") + ": " + entry.getKey();
            collectSubpipelines(ret, (FieldType)entry.getValue(), step.getValue((String)entry.getKey()), prefix);
         }
      }
      
      return (Subpipeline[])ret.toArray(new Subpipeline[ret.size()]);
   }
   
   private static void collectSubpipelines(List ret, FieldType type, Object value, String prefix) {
      if(type instanceof ListType && type.getTypes()[0] instanceof StepType) {
         Subpipeline sub = new Subpipeline(prefix, value);
         ret.add(sub);
         return;
      }
      if(type instanceof StepType) {
         Subpipeline sub = new Subpipeline(prefix, value);
         ret.add(sub);
         return;
      }
      
      if(type instanceof ListType && value instanceof Collection) {
         int ind = 0;
         for(Iterator it = ((Collection)value).iterator(); it.hasNext(); ) {
            Object ob = it.next();
            collectSubpipelines(ret, type.getTypes()[0], ob, prefix + "[" + (ind++) + "]");
         }
         return;
      }

      if(type instanceof MapType && value instanceof Map) {
         int ind = 0;
         for(Iterator it = ((Map)value).entrySet().iterator(); it.hasNext(); ) {
            Entry entry = (Entry)it.next();
            
            // highly unlikely
            collectSubpipelines(ret, type.getTypes()[0], entry.getKey(), prefix + "[" + (ind++) + "]");
            
            collectSubpipelines(ret, type.getTypes()[1], entry.getValue(), prefix + "[" + (entry.getKey()) + "]");
         }
         return;
      }
   }
}