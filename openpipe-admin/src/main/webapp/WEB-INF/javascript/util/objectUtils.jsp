<%-- Frode Johannesen (T-Rank AS)
     object utils --%>
var ObjectUtils = new Object();

ObjectUtils.builder = function() {
}
 
ObjectUtils.builder.prototype = {
   isArray:function(object) {
      return object instanceof Array;
   },
   
   isField:function(object) {
      return object != null &&
             typeof object.isField == 'function' &&
             object.isField();
   },
   
   isBasicValue:function(object) {
      return object != null &&
             typeof object.getValueType == 'function' &&
             object.getValueType() == 'basic';
   },

   isListValue:function(object) {
      return object != null &&
             typeof object.getValueType == 'function' &&
             object.getValueType() == 'list';
   },
   
   isMapValue:function(object) {
      return object != null &&
             typeof object.getValueType == 'function' &&
             object.getValueType() == 'map';
   },
   
   isEntryValue:function(object) {
      return object != null &&
             typeof object.getValueType == 'function' &&
             object.getValueType() == 'entry';
   },
   
   isErrorValue:function(object) {
      return object != null &&
             typeof object.getValueType == 'function' &&
             object.getValueType() == 'error';
   },
   
   isStepValue:function(object) {
      return object != null &&
             typeof object.getValueType == 'function' &&
             object.getValueType() == 'step';
   },

   isStep:function(object) {
      return object != null &&
             typeof object.getClassName == 'function'; <%-- TODO: weeeeaaaak :) --%>
   },
   
   copyArray:function(object) { <%-- Handles multiple dimensions --%>
      if(object instanceof Array) {
         var ret = new Array();
         for(var index in object) {
            ret[index] = object[index] instanceof Array ? this.copyArray(object[index]) : object[index];
         }
         return ret;
      }
      return object;
   }
}