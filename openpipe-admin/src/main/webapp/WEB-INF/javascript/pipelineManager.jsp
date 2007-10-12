<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Frode Johannesen (T-Rank AS)
     pipeline manager --%>
var PipelineManager = new Object();

PipelineManager.builder = function() {
   var steps = stepManager.getConfiguredSteps();
   var p = new mainpipeline.builder('MAIN');
   for(var i = 0; i < steps.length; ++i) {
      p.addStep(steps[i]);
   }

   this.pipelines = [ p ];
   this.workingPipeline = this.pipelines[0];
}
 
PipelineManager.builder.prototype = {
   getWorkingPipeline:function() {
      return this.workingPipeline;
   },
   
   addStep:function(step) {
      this.workingPipeline.addStep(step);
   },
   
   setWorkingStep:function(step) {
      this.workingStep = step;
   },
   
   getWorkingStep:function(step) {
      return this.workingStep;
   },
   
   setWorkingStepFromIndex:function(index) {
      this.workingStep = this.workingPipeline.getSteps()[index];
      return true;
   },
   
   getSubpipelines:function(pipeline) {
      this.html = "";
      
      this.currentLevel = -1;
      this.html += pipeline;

      this._pipeline = pipeline;
      this._work = [];
      this._subpipelines = [];
      this._ret = [];

      traversalUtils.traverseDepthFirst(pipeline.getSteps(), this.subpiplinesStart, this.subpiplinesEnd, "");

      this._work = null;
      this._subpipelines = null;
      this._pipeline = null;

      alert(this.html);
      
      var ret = this._ret;
      this._ret = null;
      return ret;
   },
   
   subpiplinesStart:function(element, name, level) {
      var manager = pipelineManager;
      manager._work[level] = element;
      if(objectUtils.isListValue(manager._work[level]) && objectUtils.isStepValue(element.getListType())) {
         manager.html += "\nSUBPIPELINE";
      }
      else if(objectUtils.isStepValue(element) && level > 0 && !objectUtils.isListValue(manager._work[level-1])) {
         manager.html += "\nSINGLE STEP";
      }
      
      return true;
   },
   
   subpiplinesEnd:function(element, level) {
     var manager = pipelineManager;
     manager._work[level] = null;
     manager._subpipelines[level] = null;
   },
   
   isStepMoveable:function(fromPipeline, toPipeline) {
      return true;
   }
}