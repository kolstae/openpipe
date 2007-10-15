package no.trank.openpipe.admin.gwt.server.biz;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.trank.openpipe.admin.gwt.client.model.type.BasicType;
import no.trank.openpipe.admin.gwt.client.model.type.ErrorType;
import no.trank.openpipe.admin.gwt.client.model.type.FieldType;
import no.trank.openpipe.admin.gwt.client.model.type.ListType;
import no.trank.openpipe.admin.gwt.client.model.type.MapType;
import no.trank.openpipe.admin.gwt.client.model.type.StepType;
import no.trank.openpipe.admin.gwt.client.model.type.StringType;
import no.trank.openpipe.admin.gwt.server.config.StepEdit;
import no.trank.openpipe.api.PipelineStep;

/**
 * @version $Revision$
 */
public class TypeManager {
   private static StepManager stepManager;
   
   private static List<String> primitives = Arrays.asList(new String[] {
      "boolean", "byte", "short", "char", "int", "long", "float", "double"
   });
   private static List<Class> primitiveWrappers = Arrays.asList(new Class[] {
         Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class
   });
   
   public static String getPrimitiveTypeName(Type type) {
      if (type instanceof Class) {
         if(((Class)type).isPrimitive()) {
            return type.toString();
         }

         int ind = primitiveWrappers.indexOf(type);
         if(ind != -1) {
            return primitives.get(ind);
         }
      }

      return null;
   }
   
   public static Type getTypeArgument(Type type, int index) {
      if(type instanceof ParameterizedType) {
         ParameterizedType p = (ParameterizedType)type;
         Type[] arr = p.getActualTypeArguments();
         if(index >= 0 && index < arr.length) {
            return arr[index];
         }
      }
      return null;
   }
   
   public static boolean isTypeOrSubClass(Type type, Class<?> clazz) {
      if(type instanceof ParameterizedType) {
         type = ((ParameterizedType)type).getRawType();
      }
      
      return type instanceof Class && clazz.isAssignableFrom((Class)type);
   }

   public static boolean isArray(Type type) {
      if(type instanceof ParameterizedType) {
         type = ((ParameterizedType)type).getRawType();
      }
      
      return type instanceof Class && ((Class)type).isArray();
   }
   
   public static Type getArrayType(Type type) {
      if(type instanceof ParameterizedType) {
         type = ((ParameterizedType)type).getRawType();
      }
      
      if(type instanceof GenericArrayType) {
         return ((GenericArrayType)type).getGenericComponentType();
      }

      if(type instanceof Class) {
         return ((Class)type).getComponentType();
      }
      
      return null;
   }
   
   public Object createValue(Type value, Map<String, String[]> map) {
      return createValue(value, map, "");
   }
   
   public static FieldType getFieldType(Type type) {
      if(isTypeOrSubClass(type, String.class)) {
         return StringType.getInstance();
      }
      if(isTypeOrSubClass(type, Map.class)) {
         return MapType.getInstance(
               getFieldType(getTypeArgument(type, 0)),
               getFieldType(getTypeArgument(type, 1)));
      }
      if(isTypeOrSubClass(type, List.class)) {
         return ListType.getInstance(getFieldType(getTypeArgument(type, 0)), "list");
      }
      if(isTypeOrSubClass(type, Set.class)) {
         return ListType.getInstance(getFieldType(getTypeArgument(type, 0)), "set");
      }
      if(isTypeOrSubClass(type, PipelineStep.class)) {
         return StepType.getInstance();
      }
      if(isArray(type)) {
         return ListType.getInstance(getFieldType(getArrayType(type)), "array");
      }
      if(getPrimitiveTypeName(type) != null) {
         return BasicType.getInstance(getPrimitiveTypeName(type));
      }
      return ErrorType.getInstance("Not supported: " + type.toString());
   }
   
   @SuppressWarnings("unchecked")
   private Object createValue(Type type, Map<String, String[]> map, String prefix) {
      String[] tmp = map.get(prefix);
      String oneValue = tmp != null && tmp.length > 0 ? tmp[0] : null;
      String prefixDot = prefix.length() > 0 ? prefix + "." : prefix;
      
      Class clazz = getPrimitiveWrapper(type);
      String primitiveName = getPrimitiveTypeName(type);
      if(primitiveName != null) {
         // TODO: exceptions, types, etc
          primitiveWrappers.get(primitiveName.indexOf(primitiveName));
         if(clazz == Boolean.class) {
            return "true".equals(oneValue) ? Boolean.TRUE : Boolean.FALSE;
         }
         else if(clazz == Float.class || clazz == Double.class) {
            return Double.valueOf(oneValue != null ? oneValue : "0.0");
         }
         else {
            return Long.valueOf(oneValue != null ? oneValue : "0");
         }
      }
      else if(isTypeOrSubClass(type, String.class)) {
         return oneValue;
      }
      else if(isTypeOrSubClass(type, Map.class)) {
         LinkedHashMap ret = new LinkedHashMap();
         int[] indices = getIndices(map, prefixDot + "key");
         for(int ind : indices) {
            ret.put(createValue(getTypeArgument(type, 0), map, prefixDot + "key." + ind),
                    createValue(getTypeArgument(type, 1), map, prefixDot + "value." + ind));
         }
         return ret;
      }
      else if(isTypeOrSubClass(type, List.class)) {
         List ret = new ArrayList();
         int[] indices = getIndices(map, prefix);
         for(int ind : indices) {
            ret.add(createValue(getTypeArgument(type, 0), map, prefixDot + ind));
         }
         return ret;
      }
      else if(type instanceof Class && PipelineStep.class.isAssignableFrom((Class)type)) {
         StepEdit ret = stepManager.createStepEdit(oneValue);
         try {
            LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();
            
            for(String next : getNextLevel(map, prefix)) {
               valueMap.put(next, createValue(ret.getFields().get(next), map, prefixDot + next));
            }
            ret.setValues(valueMap);
         } catch (Exception e) {
            e.printStackTrace();
         }
         return ret;
      }
      return null;
   }
   
   private List<String> getNextLevel(Map<String, String[]> map, String prefix) {
      String prefixDot = prefix.length() > 0 ? prefix + "." : prefix;
      
      List<String> ret = new ArrayList<String>();
      for(String key : map.keySet()) {
         if(key.startsWith(prefixDot) && key.lastIndexOf('.') == prefix.length()) {
            ret.add(key.substring(prefixDot.length()));
         }
      }
      
      return ret;
   }
   
   private Class getPrimitiveWrapper(Type type) {
      if(primitiveWrappers.contains(type)) {
         return (Class)type;
      }
      int ind = primitives.indexOf(type.toString());
      return ind != -1 ? primitiveWrappers.get(ind) : null;
   }

   private static int[] getIndices(Map<String, String[]> map, String prefix) {
      String prefixDot = prefix.length() > 0 ? prefix + "." : prefix;
      Set<Integer> indices = new HashSet<Integer>();
      for(Map.Entry<String, String[]> pair : map.entrySet()) {
         if(pair.getKey().startsWith(prefixDot)) {
            String value = getOneValue(pair.getValue());
            if(value != null) {
               try {
                  String tmp = pair.getKey().substring(prefix.length() + 1);
                  int ind = tmp.indexOf('.');
                  tmp = ind != -1 ? tmp.substring(0, ind) : tmp;
                  indices.add(new Integer(tmp));
               }
               catch(Exception e) {
               }
            }
         }
      }
      
      int[] ret = new int[indices.size()];
      int i = -1;
      for(Integer integer : indices) {
         ret[++i] = integer;
      }
      Arrays.sort(ret);
      return ret;
   }
   
   private static String getOneValue(String[] values) {
      if(values == null || values.length == 0 || values[0] == null) {
         return null;
      }
      String ret = values[0].trim();
      return ret.length() > 0 ? ret : null;
   }

   // spring setters
   public void setStepManager(StepManager stepManager) {
      TypeManager.stepManager = stepManager;
   }

}