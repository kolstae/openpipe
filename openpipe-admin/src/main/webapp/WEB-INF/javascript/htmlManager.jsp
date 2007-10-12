<%-- Frode Johannesen (T-Rank AS)
     html manager
     
     There are 3 "public" functions:
        renderAvailableSteps:function(tagId)  - left column. all the steps we know of
        renderSelectedSteps:function(tagId)   - middle column. the current pipeline
        renderWorkingStep:function(tagId)     - right column.
--%>
var HtmlManager = new Object();

HtmlManager.builder = function() {
}
 
HtmlManager.builder.prototype = {
   renderAvailableSteps:function(tagId) {
      var steps = stepManager.getAvailableSteps();
      var tag = document.getElementById(tagId);
      tag.innerHTML = "";
      
      for(var i = 0; i < steps.length; ++i) {
         var attrs = { "href":    "javascript:void(0)",
                       "onclick": "javascript:handleStepClick('" + steps[i].getClassName() + "')" };
                       
         domUtils.addTag(tag, "a", attrs, steps[i].getName());
         domUtils.addTag(tag, "br", null, null);
      }
   },
    
   renderSelectedSteps:function(tagId) {
      var steps = pipelineManager.getWorkingPipeline().getSteps();
      var tag = document.getElementById(tagId);
      tag.innerHTML = "";

      var workingStep = pipelineManager.getWorkingStep();
      for(var i = 0; i < steps.length; ++i) {
         if(steps[i] == workingStep) {
            domUtils.addText(tag, "> ");
         }
         var attrs = { "href":    "javascript:void(0)",
                       "onclick": "javascript:switchWorkingStep('" + i + "')" };
                       
         domUtils.addTag(tag, "a", attrs, steps[i].getName());
         domUtils.addTag(tag, "br", null, null);
      }
   },

   renderWorkingStep:function(tagId) {
      var step = pipelineManager.getWorkingStep();
      this.workingDiv = document.getElementById(tagId);
      this.workingDiv.innerHTML = '';
      
      if(step != null) {
         this.tables = [];
         this.trs = [];
         this.tds = [];
         this.modelEls = [];
         traversalUtils.traverseDepthFirst( [ step ], this.renderWorkingStepStart, this.renderWorkingStepEnd, "");
         domUtils.debug = false;
         //alert(this.workingDiv.innerHTML);
         this.tables = this.trs = this.tds = null; <%-- clean up --%>
      }
   },
   
   renderWorkingStepStart:function(element, name, level) {
      var manager = htmlManager;
      manager.workingDepth = level;
      var modelIndex = manager.modelEls.length;
      manager.modelEls[modelIndex] = element;
   
      if(level == 0) {
         manager.createTableElement('table', null);
      }
      else if(objectUtils.isField(element)) {
         manager.createTableElement('tr', null);
         manager.createTableElement('td', element.getName());
      }
      else if(objectUtils.isListValue(element)) {
         manager.createTableElement('td', null);
         if(!element.isEmpty()) {
            manager.createTableElement('table', null);
            manager.createTableElement('tr', null);
         }
      }
      else if(objectUtils.isMapValue(element)) {
         manager.createTableElement('td', null);
         if(!element.isEmpty()) {
            manager.createTableElement('table', null);
         }
      }
      else if(objectUtils.isEntryValue(element)) {
         manager.createTableElement('tr', null);
      }
      else if(objectUtils.isBasicValue(element)) {
         manager.createTableElement('td', null);
         var attrs = { 'name': name };
         attrs['onchange'] = "javascript:htmlManager.basicValueChanged(this," + modelIndex + ")";
         attrs['type'] = element.getTypeString() == 'boolean' ? 'checkbox' : 'text';
         if(!element.isEmpty()) {
            attrs[element.getTypeString() == 'boolean' ? 'checked' : 'value'] = element.getFieldValue();
         }
         domUtils.addTag(manager.tds[level], 'input', attrs, null);
      }
      else if(objectUtils.isErrorValue(element)) {
         manager.createTableElement('td', element.getMessage());
      }
      else if(objectUtils.isStepValue(element)) {
         manager.createTableElement('td', "[step: " + element.getName() + "]");
         return false; <%-- don't render subpipeline step properties --%>
      }
      
      return true;
   },
    
   renderWorkingStepEnd:function(element, level) {
      var manager = htmlManager;
      manager.tables[level] = manager.trs[level] = manager.tds[level] = null; <%-- helps keep track of containing elements --%>
   },
   
   basicValueChanged:function(input, modelIndex) {
      var manager = htmlManager;
      var element = manager.modelEls[modelIndex];
      if(element.getTypeString() == 'boolean') {
         element.setFieldValue(input.checked ? true : false);
      }
      else {
         element.setFieldValue(input.value);
      }
   },

   toString:function() {
      return "HtmlManager";
   },
   
   <%-- "private" helper function --%>
   createTableElement:function(type, content) {
      var depth = this.workingDepth;
      if(type == 'td') {
         var parent = this.trs[depth] != null ? this.trs[depth] : this.trs[depth-1];
         return this.tds[depth] = domUtils.addTag(parent, 'td', { 'valign': 'top' }, content);
      }
      if(type == 'tr') {
         var parent = this.tables[depth] != null ? this.tables[depth] : this.tables[depth-1];
         return this.trs[depth] = domUtils.addTag(parent, 'tr', null, content);
      }
      var parent = depth == 0 ? this.workingDiv : (this.tds[depth] != null ? this.tds[depth] : this.tds[depth-1]);
      return this.tables[depth] = domUtils.addTag(parent, 'table', null, content);
   }
}