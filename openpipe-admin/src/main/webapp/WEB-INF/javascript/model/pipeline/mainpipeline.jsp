<%-- Frode Johannesen (T-Rank AS)
     mainpipeline class definitions --%>
var mainpipeline = new Object();

mainpipeline.builder = function(name) {
   this.steps = new Array();
   this.name = name;
}

mainpipeline.builder.prototype = {
   getCodeFile:function() {
      return "model/pipeline/mainpipeline.jsp";
   },

   toString:function() {
      return this.getName();
   },
   
   getName:function() {
      return this.name;
   },
   
   indexOf:function(step) {
      for(var i = 0; i < this.steps.length; ++i) {
         if(step == this.steps[i]) {
            return i;
         }
      }
      return -1;
   },

   getSteps:function() {
      return this.steps;
   },
   
   addStep:function(step) {
      this.steps[this.steps.length] = step;
      return true;
   },
   
   removeStep:function(step) {
      for(var i = 0; i < this.steps.length; ++i) {
         if(this.steps[i] == step) {
            splice(this.steps, i, 1);
            return true;
         }
      }
      return false;
   }
}