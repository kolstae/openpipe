package no.trank.openpipe.admin.biz;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import no.trank.openpipe.admin.config.StepEdit;
import no.trank.openpipe.api.PipelineStep;

/**
 * @version $Revision$
 */
public class StepManager {
   private static final Logger log = LoggerFactory.getLogger(StepManager.class);
   private String configFile;
   private List<PipelineStep> pipelineSteps;
   private Map<PipelineStep, Map<String, Type>> fieldMap;
   private Map<PipelineStep, Map<String, Object>> defaultValuesMap;
     
   @SuppressWarnings("unchecked")
   public void init() {
      pipelineSteps = new ArrayList<PipelineStep>();
      fieldMap = new HashMap<PipelineStep, Map<String, Type>>();
      defaultValuesMap = new HashMap<PipelineStep, Map<String, Object>>();
      try {
         ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(configFile);
         List<String> pipelineStepClassNames = (List<String>)context.getBean("steps");
         for(String className : pipelineStepClassNames) {
            Class clazz = Class.forName(className);
            PipelineStep step = (PipelineStep)clazz.newInstance();
            pipelineSteps.add(step);
            
            // fields
            Map<String, Type> fields = new LinkedHashMap<String, Type>();
            for(PropertyDescriptor propDesc : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
               if(propDesc.getWriteMethod() != null) {
                  try {
                     fields.put(propDesc.getName(), clazz.getDeclaredField(propDesc.getName()).getGenericType());
                  }
                  catch(NoSuchFieldException e) {
                     log.debug("No field '{}' matching setter for class {}", propDesc.getName(), clazz.getName());
                  }
               }
            }
            fieldMap.put(step, fields);
            
            // default values
            setupDefaultValues(step);
         }
         
         log.info("init was a success");
      } catch(Exception e) {
         log.error("init not successful", e);
         throw new RuntimeException(e);
      }
      
      Collections.sort(pipelineSteps, new Comparator<PipelineStep>() {
         public int compare(PipelineStep o1, PipelineStep o2) {
            String n1 = o1.getName() != null ? o1.getName() : "";
            String n2 = o2.getName() != null ? o2.getName() : "";
            return n1.compareTo(n2);
         }
      });
   }
   
   public StepEdit createStepEdit(String className) {
      if(className == null || className.length() == 0) {
         return null;
      }
      
      StepEdit ret = new StepEdit();
      
      PipelineStep step = getPipelineStepFromClassName(className);
      ret.setStep(step);
      
      ret.setFields(fieldMap.get(step));
      
      return ret;
   }
   
   public List<StepEdit> getDefaultSteps() {
      List<StepEdit> ret = new ArrayList<StepEdit>();
      for(PipelineStep step : pipelineSteps) {
         StepEdit s = new StepEdit();
         s.setStep(step);
         s.setFields(fieldMap.get(step));
         s.setValues(defaultValuesMap.get(step));
         ret.add(s);
      }
      
      return ret;
   }
   
   public Map<String, Map<String, Object>> getClassDefaultsMap() {
      Map<String, Map<String, Object>> ret = new LinkedHashMap<String, Map<String, Object>>();
      for(PipelineStep step : pipelineSteps) {
         ret.put(step.getClass().getName(), defaultValuesMap.get(step));
      }
      return ret;
   }
   
   
   private PipelineStep getPipelineStepFromClassName(String className) {
      for(PipelineStep step : pipelineSteps) {
         if(step.getClass().getName().equals(className)) {
            return step;
         }
      }
      return null;
   }
   
   private void setupDefaultValues(PipelineStep step) throws Exception {
      Map<String, Object> ret = new HashMap<String, Object>();
      Class clazz = step.getClass();
      for(PropertyDescriptor propDesc : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
         if(propDesc.getWriteMethod() != null) {
            Object tmp = null;
            if(propDesc.getReadMethod() != null) {
               tmp = propDesc.getReadMethod().invoke(step, new Object[0]);
            }
            else {
               try {
                  Field f = clazz.getDeclaredField(propDesc.getName());
                  f.setAccessible(true);
                  tmp = f.get(step);
               } catch (SecurityException e) {
               } catch (NoSuchFieldException e) {
               } catch (IllegalArgumentException e) {
               } catch (IllegalAccessException e) {
               }
            }
            if(tmp instanceof Boolean && !((Boolean)tmp).booleanValue()) {
               tmp = null;
            }
            else if(tmp instanceof Iterable && !((Iterable)tmp).iterator().hasNext()) {
               tmp = null;
            }
            else if(tmp instanceof Map && ((Map)tmp).isEmpty()) {
               tmp = null;
            }
            else if(tmp instanceof Number && ((Number)tmp).intValue() == 0) {
               tmp = null;
            }
            else if(tmp != null && (tmp.toString() == null || tmp.toString().length() == 0)) {
               tmp = null;
            }
            
            if(tmp != null) {
               ret.put(propDesc.getName(), tmp);
            }
         }
      }
      defaultValuesMap.put(step, ret);
   }
   
   // spring setters
   public void setConfigFile(String configFile) {
      this.configFile = configFile;
   }
}