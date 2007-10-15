package no.trank.openpipe.admin.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import no.trank.openpipe.admin.biz.StepManager;
import no.trank.openpipe.admin.biz.TypeManager;
import no.trank.openpipe.admin.config.StepEdit;

/**
 * @version $Revision$
 */
public class AdminController extends AbstractController {
   private StepManager stepManager;
   private TypeManager typeManager;
   
   @Override
   protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
      Map<String, Object> model = new HashMap<String, Object>();
      
      model.put("classDefaultsMap", stepManager.getClassDefaultsMap());
      
      model.put("defaultSteps", stepManager.getDefaultSteps());
      model.put("configuredSteps", readPipeline(request, "pipeline.new"));
      
      return new ModelAndView("edit/editStep", model);
   }
   
   private List<StepEdit> readPipeline(HttpServletRequest request, String prefix) throws Exception {
      String stepPrefix = prefix + ".step";
      List<Integer> ind = new ArrayList<Integer>();
      
      for(Map.Entry<String, String[]> pair : ((Map<String, String[]>)request.getParameterMap()).entrySet()) {
         if(pair.getKey().startsWith(stepPrefix) &&
            pair.getKey().indexOf('.', stepPrefix.length()) == pair.getKey().lastIndexOf('.')) {
            try {
               ind.add(new Integer(pair.getKey().substring(stepPrefix.length() + 1)));
            }
            catch(Exception e) {
               e.printStackTrace();
            }
         }
      }

      List<StepEdit> ret = new ArrayList<StepEdit>();
      Collections.sort(ind);
      for(int i : ind) {
         ret.add(readStep(request, prefix + ".step." + i));
      }
      
      return ret;
   }

   
   private StepEdit readStep(HttpServletRequest request, String prefix) throws Exception {
      StepEdit stepEdit = stepManager.createStepEdit(request.getParameter(prefix));

      if(stepEdit != null) {
         Map<String, Object> values = readValues(request, prefix, stepEdit.getFields());
         
         stepEdit.setValues(values);
         
         return stepEdit;
      }
      return null;
   }
   
   @SuppressWarnings("unchecked")
   private Map<String, Object> readValues(HttpServletRequest request, String prefix, Map<String, Type> fieldMap) {
      Map<String, Map<String, String[]>> fieldToParameterMap = new HashMap<String, Map<String, String[]>>();
      for(Map.Entry<String, String[]> pair : ((Map<String, String[]>)request.getParameterMap()).entrySet()) {
         String tmp = pair.getKey();
         if(tmp.startsWith(prefix + ".")) {
            tmp = tmp.substring(prefix.length() + 1);
            int ind = tmp.indexOf('.');
            String fieldName = ind != -1 ? tmp.substring(0, ind) : tmp;
            tmp = ind != -1 ? tmp.substring(ind+1) : "";
            Map<String, String[]> map = fieldToParameterMap.get(fieldName);
            if(map == null) {
               fieldToParameterMap.put(fieldName, map = new HashMap<String, String[]>());
            }
            map.put(tmp, pair.getValue());
         }
      }
      
      Map<String, Object> values = new HashMap<String, Object>();
      for(Map.Entry<String, Type> pair : fieldMap.entrySet()) {
         Map<String, String[]> map = fieldToParameterMap.get(pair.getKey());
         if(map != null) {
            values.put(pair.getKey(), typeManager.createValue(pair.getValue(), map));
         }
      }

      return values;
   }

   // spring setters
   public void setStepManager(StepManager stepManager) {
      this.stepManager = stepManager;
   }

   public void setTypeManager(TypeManager typeManager) {
      this.typeManager = typeManager;
   }
}