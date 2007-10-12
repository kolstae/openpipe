<%-- Frode Johannesen (T-Rank AS)
     subpipeline class definitions --%>
var subpipeline = new Object();
var nextPipelineId = 0;

subpipeline.builder = function(fieldValue, surroundingValue) {
   this.fieldValue = fieldValue;
   this.surroundingValue = surroundingValue;
   if(fieldValue == null) {
      this.steps = new Array();
   }
   this.id = nextPipelineId++;
}

subpipeline.builder.prototype = {
   getCodeFile:function() {
      return "model/subpipeline.jsp";
   },

   toString:function() {
      return this.fieldValue.getValueType() + " " + this.surroundingValue.getValueType();
   },
   
   getFieldValue:function() {
      return this.fieldValue;
   },
   
   getSurroundingValue:function() {
      return this.surroundingValue;
   },
   
   indexOf:function(step) {
      getSteps();
      for(var i = 0; i < this.steps.length; ++i) {
         if(step == this.steps[i]) {
            return i;
         }
      }
      return -1;
   },
   
   getId:function() {
      return this.id;
   },
   
   setWorkingStep:function(step, prefix) {
       this.updateWorkingStep();
       this.workingStep = step;
       this.workingPrefix = prefix;
    },
    
    updateWorkingStep:function() {
        if(this.workingStep != null) {
           this.workingStep.updateFromHtml();
        }
//        this.setupSubpipelines();
    },
   
   
    setWorkingStepFromIndex:function(pipelineIndex, index) {
       this.getSteps();
       if(this.steps[index] != this.workingStep) {
          this.setWorkingStep(this.steps[index], this.workingPrefix);
          return true;
       }
       return false;
    },
   
   getSteps:function() {
      if(this.surroundingValue == null) {
         return this.steps;
      }
   
      var ret = new Array();
      if(this.surroundingValue.getValueType() == 'step') {
         if(!this.surroundingValue.isEmpty()) {
            ret[0] = this.surroundingValue;
         }
      }
      else {
         var tmp = this.surroundingValue.getValues();
         for(var i = 0; i < tmp.length; ++i) {
            if(!tmp[i].isEmpty()) {
               ret[ret.length] = tmp[i];
            }
         }
      }
      this.steps = ret;
      return ret;
   },
   
   addStep:function(step) {
       if(this.surroundingValue == null) { // main pipeline
          this.steps[this.steps.length] = step;
          return true;
       }
   
       if(this.surroundingValue.getValueType() == 'step') {
          if(this.surroundingValue.isEmpty()) {
             this.surroundingValue.setStep(step);
             return true;
          }
          else {
             return false;
          }
       }
       
       var tmp = this.surroundingValue.getValues();
       tmp[tmp.length] = step;
       return true;
   },
    
   removeStep:function(step) {
      if(this.surroundingValue == null) { // main pipeline
          this.steps[this.steps.length] = step;
          var newSteps = new Array();
          for(var i = 0; i < this.steps.length; ++i) {
             if(this.steps[i] != step) {
                newSteps[newSteps.length] = this.steps[i];
             }
             else {
                ret = true;
             }
          }
          this.values = newSteps;
          return ret;
       }
       
       var tmp = this.surroundingValue.getValues();
       var newSteps = new Array();
       for(var i = 0; i < tmp.length; ++i) {
          if(tmp[i] != step) {
             newSteps[newSteps.length] = tmp[i];
          }
          else {
             ret = true;
          }
       }
       this.surroundingValue.setValues(newSteps);
       return ret;
    }
}