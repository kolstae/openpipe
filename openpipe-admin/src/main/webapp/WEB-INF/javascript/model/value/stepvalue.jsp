<%-- Frode Johannesen (T-Rank AS)
     stepvalue class definitions --%>
var stepvalue = new Object();

stepvalue.builder = function(value) {
   this.value = value;
}

stepvalue.builder.prototype = {
   getCodeFile:function() {
      return "model/value/stepvalue.jsp";
   },

    toString:function() {
       var ret = "";
       if(this.value != null) {
          if(this.value.getField('name') != null &&
             this.value.getField('name').getValue() != null &&
             this.value.getField('name').getValue().getFieldValue != null) {
             this.value.getField('name').getValue().getFieldValue;
          }
       }
       return ret + "(STEP)";
    },
    
    getValueType:function() {
       return "step";
    },
    
    getTypeString:function() {
       return "STEP";
    },
    
    getChildValues:function() {
       return this.isEmpty() ? [] : this.value.getChildValues();
    },
    
    getStep:function() {
       return this.value;
    },
    
    setStep:function(step) {
       this.value = step;
    },
    
    createSibling:function() {
       return new stepvalue.builder(null);
    },
    
    getClone:function() {
       return new stepvalue.builder(this.value);
    },
    
    isEmpty:function() {
       return this.value == null || this.value.isEmpty();
    },
    
    updateFromHtml:function() {
//       this.value = null; // TODO
    },
    
    getValueHtml:function(name) {
       return "step";
    },
    
    getHtml:function(name) {
       this.workingPrefix = name;
       if(this.value != null) {
          return "is not null ...";
       }
       return "is null";
    },
    
    collectValues:function(valueCollector, prefix) {
       if(!this.isEmpty()) {
          this.value.collectValues(valueCollector, prefix);
       }
    }
}