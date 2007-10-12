<%-- Frode Johannesen (T-Rank AS)
     listpipeline class definitions --%>
var listpipeline = new Object();

listpipeline.builder = function(parentPipeline, name, outerValue, listValue) {
   this.parentPipeline = parentPipeline;
   this.name = name;
   this.outerValue = outerValue;
   this.listValue = listValue;
}

listpipeline.builder.prototype = {
   getCodeFile:function() {
      return "model/pipeline/listpipeline.jsp";
   },

   toString:function() {
      return this.getName();
   },
   
   getName:function() {
      return this.name;
   },
   
   getParentPipeline:function() {
      return this.parentPipeline;
   },
   
   indexOf:function(step) {
      var steps = this.getSteps();
      for(var i = 0; i < steps.length; ++i) {
         if(steps[i] == step) {
            return i;
         }
      }
      return -1;
   },

   getSteps:function() {
      var ret = new Array();
      var tmp = this.listValue.getValues();
      for(var i = 0; i < tmp.length; ++i) {
         if(!tmp[i].isEmpty()) {
            ret[ret.length] = tmp[i];
         }
      }
      return ret;
   },
   
   addStep:function(step) {
      var steps = this.getSteps();
      steps[steps.length] = step;
      this.listValue.setValues(steps);
      return true;
   },
   
   removeStep:function(step) {
      var steps = this.getSteps();
      
      for(var i = 0; i < steps.length; ++i) {
         if(steps[i] == step) {
            splice(steps, i, 1);
            this.listValue.setValues(steps);
            return true;
         }
      }
      return false;
   }
}