<%-- Frode Johannesen (T-Rank AS)
     value class definitions --%>
var mapvalue = new Object();

mapvalue.builder = function(keytype, valuetype) {
   this.type = 'map';
   this.maptype = new mapvalueEntry.builder(keytype, valuetype);
   this.values = new Array();
}

mapvalue.builder.prototype = {
   getCodeFile:function() {
      return "model/value/mapvalue.jsp";
   },
   
   put:function(key, value) {
      this.values[this.values.length] = new mapvalueEntry.builder(key, value);
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
    
    getValueType:function() {
       return "map";
    },
    
    getTypeString:function() {
       return "MAP&lt;" + this.maptype.getKey().getTypeString() + ", " + this.maptype.getValue().getTypeString() + "&gt;";
    },
    
    createSibling:function() {
       return new mapvalue.builder(this.maptype.getKey(), this.maptype.getValue());
    },
    
    getClone:function() {
       var newValues = new Array();
       var ret = new mapvalue.builder(this.maptype.getKey(), this.maptype.getValue());
       for(var i = 0; i < this.values.length; ++i) {
          var c = this.values[i].getClone();
          ret.put(c.getKey(), c.getValue());
       }
    },
    
    isEmpty:function() {
       return this.values == null || this.values.length == 0;
    },
    
    updateFromHtml:function() {
//       var newValues = new Array();
//       for(var i = 0; i < this.values.length; ++i) {
  //        this.values[i][0].updateFromHtml();
    //      this.values[i][1].updateFromHtml();
      //    if(!this.values[i][0].isEmpty()) {
        //     newValues[newValues.length] = this.values[i];
//          }
  //     }
    //   this.values = newValues;
    },
        
    getValueHtml:function(name) {
//       this.workingPrefix = name;
  //  
//       var ret = '';
  //     
    //   var appendValue = this.values.length == 0 || this.values[this.values.length-1][0].isEmpty();
      // this.values[this.values.length] = [ this.maptype.getKey().createSibling(), this.maptype.getValue().createSibling() ];
//       
  //     ret += '<table>';
    //   for(var i = 0; i < this.values.length; ++i) {
      //    ret += '<tr>';
        //  ret += '<td valign=top>' + this.values[i][0].getValueHtml(name + ".key." + i) + "</td><td valign=top>" +
//                 this.values[i][1].getValueHtml(name + ".value." + i) + "</td>";
  //        ret += '</tr>';
    //   }
      // ret += '</table>';
//              
  //     return ret;
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
   
   collectValues:function(valueCollector, prefix) {
      var index = 0;
      for(var i = 0; i < this.values.length; ++i) {
         if(!this.values[i].isEmpty()) {
            this.values[i].getKey().collectValues(valueCollector, prefix + ".key." + index);
            this.values[i].getKey().collectValues(valueCollector, prefix + ".value." + (index++));
         }
      }
   },
   
   getSubpipelines:function(outerValue, wrapperValue) {
      var ret = new Array();
      if(this.maptype.getValue().getValueType() == 'step') {
         for(var i = 0; i < this.values.length; ++i) {
            if(!this.values[i][0].isEmpty()) {
               ret[ret.length] = new subpipeline.builder(outerValue, values[i][1]);
            }
         }
      }
      else {
         for(var i = 0; i < this.values.length; ++i) {
            if(!this.values[i][0].isEmpty()) {
               var arr = this.values[i][1].getSubpipelines(outerValue, this);
               if(arr != null) {
                  for(var j = 0; j < arr.length; ++j) {
                     ret[ret.length] = arr[j];
                  }
               }
            }
         }
      }
      return ret;
  }
}

var mapvalueEntry = new Object();

mapvalueEntry.builder = function(key, value) {
   this.key = key;
   this.value = value;
}

mapvalueEntry.builder.prototype = {
   getKey:function() {
      return this.key;
   },
   
   setKey:function(key) {
      this.key = key;
   },
   
   getValue:function() {
      return this.value;
   },
   
   setValue:function(value) {
      this.value = value;
   },
   
   getClone:function() {
      return new mapvalueEntry.builder(this.key, this.value);
   },
   
   toString:function() {
      this.key.toString() + ": " + this.value.toString();
   },
   
   isEmpty:function() {
      return this.key.isEmpty();
   },
   
   getChildValues:function() {
      return [ this.key, this.value ];
   },
   
   getValueType:function() {
      return "entry";
   }
}