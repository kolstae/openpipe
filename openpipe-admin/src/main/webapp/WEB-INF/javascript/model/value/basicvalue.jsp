<%-- Frode Johannesen (T-Rank AS)
     value class definitions --%>
var basicvalue = new Object();

basicvalue.builder = function(valueType, fieldValue) {
   this.type = 'basic';
   this.valueType = valueType;
   this.fieldValue = fieldValue;
}


basicvalue.builder.prototype = {
   getCodeFile:function() {
      return "model/value/basicvalue.jsp";
   },

    toString:function() {
       if(this.valueType == 'string')
          return "'" + this.fieldValue + "'(" + this.valueType + ")";
       return this.fieldValue + "(" + this.valueType + ")";          
    },
    
    getValueType:function() {
       return "basic";
    },
    
    getChildValues:function() {
       return null;
    },
    
    getFieldValue:function() {
       return this.fieldValue;
    },
    
    setFieldValue:function(value) {
       return this.fieldValue = value;
    },
    
    getTypeString:function() {
       return this.valueType;
    },
    
    createSibling:function() {
       return new basicvalue.builder(this.valueType, null);
    },
    
    getClone:function() {
       return new basicvalue.builder(this.valueType, this.fieldValue);
    },
    
    isEmpty:function() {
       if(this.valueType == 'boolean') {
          return this.fieldValue != true;
       }
       else {
          return this.fieldValue == null || this.fieldValue.length == 0;
       }
    },
    
    updateFromHtml:function() {
       var el = document.getElementById(this.workingPrefix);
       if(this.valueType == 'boolean') {
          this.fieldValue = el.checked ? true : false;
       }
       else {
          this.fieldValue = el.value != null ? el.value : "";
       }
    },
    
    getValueHtml:function(name) {
       this.workingPrefix = name;
       if(this.valueType == 'boolean') {
          return '<input type="checkbox" id="' + name + '" value="true"' +
                 (this.fieldValue == true ? ' checked' : '') + ' />';
       }
       return '<input type="text" id="' + name + '"' +
              (this.fieldValue != null && (this.fieldValue + "").length > 0 ? ' value="' + this.fieldValue + '"' : '') +
              ' />';
    },
    
    collectValues:function(valueCollector, prefix) {
       if(!this.isEmpty()) {
          if(this.valueType == 'boolean') {
             valueCollector.add(prefix, 'true');
          }
          else {
             valueCollector.add(prefix, this.fieldValue);
          }
       }
    },
    
    getSubpipelines:function(outerValue, wrapperValue) {
       return null;
    }
}