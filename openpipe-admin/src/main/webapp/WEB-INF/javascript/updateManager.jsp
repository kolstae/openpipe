<%-- Frode Johannesen (T-Rank AS)
     update manager --%>
var UpdateManager = new Object();

UpdateManager.builder = function() {
}
 
UpdateManager.builder.prototype = {
   debugPostPipeline:function() {
      pipelineManager.getSubpipelines(pipelineManager.getWorkingPipeline());
   
      this.debugdiv = document.getElementById('debug');
      this.debugdiv.innerHTML = '';
      this.inDebug = true;

      this.postdiv = domUtils.addTag(this.postdiv, 'table', null, null); 
       
      var steps = pipelineManager.getWorkingPipeline().getSteps();
      traversalUtils.traverseDepthFirst(steps, this.postCallback, null, "pipeline.new.step");
   },
    
   postWorkingPipeline:function() {
      this.postdiv = document.getElementById('postformdiv');
      this.postdiv.innerHTML = '';
      this.inDebug = false;
      var steps = pipelineManager.getWorkingPipeline().getSteps();
      traversalUtils.traverseDepthFirst(steps, this.postCallback, null, "pipeline.new.step");
      document.forms[0].submit();
   },

   postCallback:function(element, name, level) {
      var value = null;
       
      if(objectUtils.isField(element)) {
         value = null;
      }
      else if(objectUtils.isStep(element)) {
         value = element.getClassName();
      }
      else if(objectUtils.isStepValue(element)) {
         value = element.getStep().getClassName();
      }
      else if(objectUtils.isBasicValue(element)) {
         value = element.getFieldValue();
      }
      
      if(value != null) {
         if(updateManager.inDebug) {
            var tr = domUtils.addTag(updateManager.debugdiv, 'tr', null, null);
            domUtils.addTag(tr, 'td', null, name + ":");
            domUtils.addTag(tr, 'td', null, value);
         }
         else {
            var input = domUtils.addTag(updateManager.postdiv, 'input',
                                          {'type': 'hidden', 'name': name, 'value': value });
         }
      }
      return true;
   }
}