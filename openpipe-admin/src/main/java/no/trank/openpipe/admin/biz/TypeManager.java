package no.trank.openpipe.admin.biz;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.trank.openpipe.admin.config.StepEdit;
import no.trank.openpipe.api.PipelineStep;

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
   
   public static boolean isStringType(Type type) {
      return type == String.class;
   }
   
   public static Type getListType(Type type) {
      if(type instanceof ParameterizedType) {
         ParameterizedType p = (ParameterizedType)type;
         if(isListType(p.getRawType())) {
            return p.getActualTypeArguments()[0];
         }
      }
      return null;
   }
   
   public static Type getMapKeyType(Type type) {
      if(type instanceof ParameterizedType) {
         ParameterizedType p = (ParameterizedType)type;
         if(isMapType(p.getRawType())) {
            return p.getActualTypeArguments()[0];
         }
      }
      return null;
   }

   public static Type getMapValueType(Type type) {
      if(type instanceof ParameterizedType) {
         ParameterizedType p = (ParameterizedType)type;
         if(isMapType(p.getRawType())) {
            return p.getActualTypeArguments()[1];
         }
      }
      return null;
   }
   
   public Object createValue(Type value, Map<String, String[]> map) {
      return createValue(value, map, "");
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
      else if(isStringType(type)) {
         return oneValue;
      }
      else if(isMapType(type)) {
         LinkedHashMap ret = new LinkedHashMap();
         int[] indices = getIndices(map, prefixDot + "key");
         for(int ind : indices) {
            ret.put(createValue(getMapKeyType(type), map, prefixDot + "key." + ind),
                    createValue(getMapValueType(type), map, prefixDot + "value." + ind));
         }
         return ret;
      }
      else if(isListType(type)) {
         List ret = new ArrayList();
         //int[] indices = getIndices(map, prefixDot + "list");
         int[] indices = getIndices(map, prefix);
         for(int ind : indices) {
            //ret.add(createValue(getListType(type), map, prefixDot + "list." + ind));
            ret.add(createValue(getListType(type), map, prefixDot + ind));
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

   private static boolean isListType(Type type) {
      if(type instanceof ParameterizedType) {
         type = ((ParameterizedType)type).getRawType();
      }
      
      return type instanceof Class && List.class.isAssignableFrom((Class) type);
   }
   
   private static boolean isMapType(Type type) {
      if(type instanceof ParameterizedType) {
         type = ((ParameterizedType)type).getRawType();
      }
      
      return type instanceof Class && Map.class.isAssignableFrom((Class) type);
   }

   // spring setters
   public void setStepManager(StepManager stepManager) {
      TypeManager.stepManager = stepManager;
   }
}