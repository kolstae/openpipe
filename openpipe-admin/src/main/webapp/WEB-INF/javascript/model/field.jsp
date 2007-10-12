<%-- Frode Johannesen (T-Rank AS)
     field class definitions --%>
var field = new Object();

field.builder = function(name, value) {
	this.name = name;
	this.value = value;
}

field.builder.prototype = {
   getCodeFile:function() {
      return "model/field.jsp";
   },
   
   isField:function() {
      return true;
   },

    toString:function() {
        var ret = "field: " + this.name;
        if(this.value != null) {
           ret += " -- " + this.value.toString();
        }
        return ret;
    },
    
    getName:function() {
       return this.name;
    },
    
    getValue:function() {
       return this.value;
    },
    
    
    getChildValues:function() {
       return [ this.value ];
    },
    
    updateFromHtml:function() {
       this.value.updateFromHtml();
    },
    
    getValueType:function() {
       return "field";
    },
    
    getClone:function() {
       return new field.builder(this.name, this.value.getClone());
    },
    
    getHtml:function(name) {
       return this.value.getValueHtml(name);
    },
    
    collectValues:function(valueCollector, prefix) {
      this.value.collectValues(valueCollector, prefix + "." + this.name);
    },
    
    getSubpipelines:function() {
       if(this.value.getValueType == 'step') {
          return [ subpipeline.builder(this.value, this.value) ];
       }
       return this.value.getSubpipelines(this.value, null);
    }
}