package no.trank.openpipe.admin.taglib;

import java.lang.reflect.Type;

import no.trank.openpipe.admin.biz.TypeManager;
import no.trank.openpipe.api.PipelineStep;

@SuppressWarnings("static-access")
public class Functions {
   private static TypeManager typeManager;
   
   public static String getPrimitiveTypeName(Type type) {
      return typeManager.getPrimitiveTypeName(type);
   }
   
   public static boolean isStringType(Type type) {
      return typeManager.isStringType(type);
   }
   
   public static Type getListType(Type type) {
      return typeManager.getListType(type);
   }
   
   public static Type getMapKeyType(Type type) {
      return typeManager.getMapKeyType(type);
   }

   
   public static Type getMapValueType(Type type) {
      return typeManager.getMapValueType(type);
   }

   public static boolean isStepType(Type type) {
      return type instanceof Class ? PipelineStep.class.isAssignableFrom((Class)type) : false;
   }
   
   // spring setters
   public void setTypeManager(TypeManager typeManager) {
      Functions.typeManager = typeManager;
   }
}