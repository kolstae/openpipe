<%-- Frode Johannesen (T-Rank AS)
     value class definitions --%>
var listvalue = new Object();

listvalue.builder = function(listtype, values) {
   this.type = 'list';
   this.listtype = listtype;
   this.values = values;
}


listvalue.builder.prototype = {
   getCodeFile:function() {
      return "model/value/listvalue.jsp";
   },

    toString:function() {
       var ret = this.getTypeString() + " [ ";
       for(var i = 0; i < this.values.length; ++i) {
          if(i > 0) ret += ", "
          ret += this.values[i].toString();
       }
       ret += " ]";
       return ret;
    },
    
    getChildValues:function() {
       var ret = new Array();
       for(var i = 0; i < this.values.length; ++i) {
          if(!this.values[i].isEmpty()) {
             ret[ret.length] = this.values[i];
          }
       }
       return ret;
    },
    
    
    getValueType:function() {
       return "list";
    },
    
   getListType:function() {
      return this.listtype;
   },
    
    getValues:function() {
       return this.values;
    },
    
    setValues:function(values) {
       this.values = values;
    },
    
    getTypeString:function() {
       return "LIST&lt;" + this.listtype.getTypeString() + "&gt;";
    },

    createSibling:function() {
       return new listvalue.builder(this.listtype, new Array());
    },
    
    getClone:function() {
       var newValues = new Array();
       for(var i = 0; i < this.values.length; ++i) {
          newValues[i] = this.values[i].getClone();
       }
       return new listvalue.builder(this.listtype, newValues);
    },
    
    isEmpty:function() {
       return this.values == null || this.values.length == 0;
    },
    
    updateFromHtml:function() {
       var newValues = new Array();
       for(var i = 0; i < this.values.length; ++i) {
          this.values[i].updateFromHtml();
          if(!this.values[i].isEmpty()) {
             newValues[newValues.length] = this.values[i];
          }
       }
       this.values = newValues;
    },
    
    
    getValueHtml:function(name) {
       this.workingPrefix = name;
    
       var ret = '';
       var i = 0;
       
       ret += '<table>';
       var appendValue = this.values.length == 0 || this.values[this.values.length-1].isEmpty();
       this.values[this.values.length] = this.listtype.createSibling();

       for(; i < this.values.length; ++i) {
          ret += '<tr><td>' + this.values[i].getHtml(name + ".list." + i) + "</td></tr>";
       }
       ret += '</table>';

       return ret;
   },

   collectValues:function(valueCollector, prefix) {
      var index = 0;
      for(var i = 0; i < this.values.length; ++i) {
         if(!this.values[i].isEmpty()) {
            this.values[i].collectValues(valueCollector, prefix + "." + (index++));
         }
      }
   },
   
   getSubpipelines:function(outerValue, wrapperValue) {
      if(this.listtype.getValueType() == 'step') {
         return [ new subpipeline.builder(outerValue, this) ];
      }
      else {
         var ret = new Array();
         for(var i = 0; i < this.values.length; ++i) {
            if(!this.values[i].isEmpty()) {
               var arr = this.steps[i].getSubpipelines();
               if(arr != null) {
                  for(var j = 0; j < arr.length; ++j) {
                     this.subpipelines[this.subpipelines.length] = arr[j];
                  }
               }
            }
         }
         return ret;
      }
   }
}