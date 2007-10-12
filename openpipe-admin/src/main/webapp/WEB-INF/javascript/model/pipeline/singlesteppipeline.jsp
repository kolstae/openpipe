<%-- Frode Johannesen (T-Rank AS)
     singlesteppipeline class definitions --%>
var singlesteppipeline = new Object();

singlesteppipeline.builder = function(parentPipeline, name, value) {
   this.parentPipeline = parentPipeline;
   this.name = name;
   this.value = value;
}

singlesteppipeline.builder.prototype = {
   getCodeFile:function() {
      return "model/pipeline/singlesteppipeline.jsp";
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
      return this.value.getStep() == step ? 0 : -1;
   },

   getSteps:function() {
      return this.steps;
   },
   
   addStep:function(step) {
      if(this.value.isEmpty()) {
         this.value.setStep(step);
         return true;
      }
      
      return false;
   },
   
   removeStep:function(step) {
      if(!this.value.isEmpty()) {
         this.value.setStep(step.createSibling());
         return true;
      }
      
      return false;
   }
}