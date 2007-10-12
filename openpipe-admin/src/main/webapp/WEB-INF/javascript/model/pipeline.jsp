<%-- Frode Johannesen (T-Rank AS)
     pipeline class definitions --%>
var pipeline = new Object();

pipeline.builder = function(steps) {
   this.subpipelines = [ new subpipeline.builder(null, null) ];
}

pipeline.builder.prototype = {
   getCodeFile:function() {
      return "model/pipeline.jsp";
   },

    toString:function() {
      return "PIPELINE";
    },

    addStep:function(step) {
       this.subpipelines[0].addStep(step);
    },

    setWorkingStepFromIndex:function(pipelineIndex, index) {
       return this.subpipelines[pipelineIndex].setWorkingStepFromIndex(index);
    },

    getWorkingPipeline:function() {
       return null;
    },

    getWorkingStep:function() {
       var workingPipeline = getWorkingPipeline();
       return workingPipeline != null ? workingPipeline.getWorkingStep() : null;
    },
    
    getWorkingStepPipelineIndex:function() {
       for(var i = 0; i < this.steps.length; ++i) {
          if(this.steps[i] == this.workingStep) {
             return -1;
          }
       }

       if(this.subpipelines != null) {
          for(var i = 0; i < this.subpipelines.length; ++i) {
             var tmp = this.subpipelines[i].getSteps();
             for(var j = 0; j < this.steps.length; ++j) {
                if(tmp[j] == this.workingStep) {
                   return i;
                }
             }
          }
       }
       
       return -2;
    },
    
    moveWorkingStepToPipeline:function(pipelineIndex) {
       var currentPipelineIndex = this.getWorkingStepPipelineIndex();
       if(pipelineIndex != currentPipelineIndex) {
          alert('moving step to pipelineIndex: ' + pipelineIndex);
          var added = pipelineIndex == -1 ? this.addStep(this.workingStep) : this.subpipelines[pipelineIndex].addStep(this.workingStep);
          if(added) {
             if(currentPipelineIndex == -1) {
                this.removeStep(this.workingStep);
             }
             else {
                this.subpipelines[pipelineIndex].removeStep(this.workingStep);
             }
             return true;
          }
       }
       return false;
    },

    indexOf:function(step) {
      for(var i = 0; i < this.steps.length; ++i) {
         if(step == this.steps[i]) {
            return i;
         }
      }
      return -1;
   },
    
    removeStep:function(step) {
       var ret = false;
       var newSteps = new Array();
       for(var i = 0; i < this.steps.length; ++i) {
          if(this.steps[i] != step) {
             newSteps[newSteps.length] = this.steps[i];
          }
          else {
             ret = true;
          }
       }
       this.steps = newSteps;
       return ret;
    },
    
    getWorkingStepHtml:function() {
       if(this.workingStep == null) {
          return "";
       }
       return "";
    
       var ret = "";
       ret += "[" + stepManager.getDefaultNameFromClassName(this.workingStep.getClassName()) + "]";
       var index = this.getWorkingStepIndex();
       ret += "&nbsp;";
       if(index > 0) {
          ret += '<a href="javascript:moveWorkingStep(true)">Move up</a>';
       }
       else {
          ret += '<i>Move up</i>';
       }
       ret += "&nbsp;";
       if(index != -1 && index < this.steps.length - 1) {
          ret += '<a href="javascript:moveWorkingStep(false)">Move down</a>';
       }
       else {
          ret += '<i>Move down</i>';
       }
       if(this.subpipelines != null && this.subpipelines.length > 0) {
          var c = 0;
          var tmp = "";
       
          tmp += " &nbsp; Move to";
          tmp += "<select onchange='javascript:moveWorkingStepToPipeline(this.value)'>";
          tmp += "<option value=.>Choose</option>";
          
          tmp += "<option value=-1>MAIN</option>";
          for(var i = 0; i < this.subpipelines.length; ++i) {
             if(this.subpipelines[i].getFieldValue() != this.workingStep) {
                ++c;
                tmp += "<option value=" + i + ">" + this.subpipelines.toString() + "</option>";
             }
          } 

          tmp += "</select>";
          if(c > 0) {
             ret += tmp;
          }
       }
       
       
       ret += "<br />";
       ret += this.workingStep.getHtml(this.workingPrefix);
       return ret;
    },
    
    
    
    moveWorkingStep:function(up) {
       var index = this.getWorkingStepIndex();
       var switchWith = index == -1 ? -1 : index + (up ? -1 : 1);
       
       if(switchWith != -1) {
          this.steps[index] = this.steps[switchWith];
          this.steps[switchWith] = this.workingStep;
          this.updateWorkingStep();
          return true;
       }
       
       return false;
    },
    
    getWorkingStepIndex:function() {
       return this.indexOf(this.workingStep);
    },
    
    updateWorkingStep:function() {
       this.subpipelines[0].updateWorkingStep();
    },
    
    getHtml:function() {
       var ret = "";
       
       ret += this.subpipelines[0].getHtml();

       if(this.subpipelines != null) {
          for(var i = 1; i < this.subpipelines.length; ++i) {
             ret += "<br /><br />";
             ret += this.subpipelines[i].toString() + "<br />";
             ret += this.subpipelines[i].getHtml(i);
          }
       }
       
       return ret;
    },
    
    setupSubpipelines:function() {
       this.subpipelines = new Array();
       return;
       for(var i = 0; i < this.steps.length; ++i) {
          var arr = this.steps[i].getSubpipelines();
          if(arr != null) {
             for(var j = 0; j < arr.length; ++j) {
                this.subpipelines[this.subpipelines.length] = arr[j];
             }
          }
       }
    },
    
    collectValues:function(valueCollector, prefix) {
//       this.updateWorkingStep();
       
       if(this.workingStep != null) {
          valueCollector.add("workingStepIndex", "" + this.getWorkingStepIndex());
       }
       
       valueCollector.add('size', this.steps.length);
       for(var i = 0; i < this.steps.length; ++i) {
          this.steps[i].collectValues(valueCollector, prefix + ".step." + i);
       }
    }
}