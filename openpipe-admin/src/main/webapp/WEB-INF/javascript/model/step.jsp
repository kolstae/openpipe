<%-- Frode Johannesen (T-Rank AS)
     step class definitions --%>
var step = new Object();

step.builder = function(className) {
	this.className = className;
	this.fields = new Array();
}

step.builder.prototype = {
   getCodeFile:function() {
      return "model/step.jsp";
   },

    toString:function() {
        return "STEP&lt;" + this.className + "&gt;";
    },
    
    getChildValues:function() {
       return this.fields;
    },
    
    getTypeString:function() {
       return "STEP";
    },

    getValueType:function() {
       return "step";
    },
    
    getName:function() {
       var f = this.getField('name');
       if(f == null || f.getValue() == null || f.getValue().getFieldValue() == null) {
          return "MISSING NAME";
       }
       return f.getValue().getFieldValue();
    },

    addField:function(field) {
       for(var i = 0; i < this.fields.length; ++i) {
          if(this.fields[i].getName() == field.getName()) {
             this.fields[i] = field;
             return;
          }
       }
       this.fields[this.fields.length] = field;
    },
    
    getField:function(key) {
       for(var i = 0; i < this.fields.length; ++i) {
          if(this.fields[i].getName() == key) {
             return this.fields[i];
          }
       }
       return null;
    },

    getClickHtml:function() {
       return '<a href="javascript:void(0)" onclick="javascript:handleStepClick(\'' +
              this.className + '\')">' + this.getField('name').getValue().getFieldValue() + '</a>';
    },
    
    getValueType:function() {
       return "step";
    },
    
    isEmpty:function() {
       return this.className == null || this.className.length == 0;
    },
    
    createSibling:function() {
       return new step.builder(this.className);
    },


    
    getHtml:function(prefix) {
      this.workingPrefix = prefix;
      var ret = '<table>';
      for(var i = 0; i < this.fields.length; ++i) {
        ret += "<tr><td valign=top>" + this.fields[i].getName() + "</td><td valign=top>" +
               this.fields[i].getHtml(prefix + ".field." + i) +
               "</td></tr>";
      }
      ret += '</table>';
      return ret;
    },
    
    getValueHtml:function(prefix) {
       return "step";
    },
    
    updateFromHtml:function() {
        for(var i = 0; i < this.fields.length; ++i) {
           this.fields[i].updateFromHtml();
        }
    },
    
    
    getClassName:function() {
       return this.className;
    },
    
    
    getClone:function() {
       var ret = new step.builder(this.className);
       for(var i = 0; i < this.fields.length; ++i) {
          ret.addField(this.fields[i].getClone());
       }
       return ret;
    },
    
    collectValues:function(valueCollector, prefix) {
       valueCollector.add(prefix, this.className);
       for(var i = 0; i < this.fields.length; ++i) {
          this.fields[i].collectValues(valueCollector, prefix);
       }
    },
    
    getSubpipelines:function(outervalue, wrapperValue) {
       var ret = new Array();
       for(var i = 0; i < this.fields.length; ++i) {
          var arr = this.fields[i].getSubpipelines();
          if(arr != null) {
             for(var j = 0; j < arr.length; ++j) {
                ret[ret.length] = arr[j];
             }
          }
       }
       return ret;
    }
}