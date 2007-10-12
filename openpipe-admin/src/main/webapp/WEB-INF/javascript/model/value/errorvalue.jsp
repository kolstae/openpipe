<%-- Frode Johannesen (T-Rank AS)
     errorvalue class definitions --%>
var errorvalue = new Object();

errorvalue.builder = function(message) {
   this.message = message;
}


errorvalue.builder.prototype = {
   getCodeFile:function() {
      return "model/value/errorvalue.jsp";
   },

    toString:function() {
       return "ERROR(" + this.message + ")";
    },
    
    getValueType:function() {
       return "error";
    },
    
    getTypeString:function() {
       return "ERROR";
    },
    
    
    getChildValues:function() {
       return null;
    },

    getMessage:function() {
       return this.message;
    },
    
    createSibling:function() {
       return new errorvalue.builder('');
    },
    
    getClone:function() {
       return new errorvalue.builder(this.message);
    },
    
    isEmpty:function() {
       return false;
    },
    
    updateFromHtml:function() {
    },
    
    getValueHtml:function(name) {
       return this.toString();
    },
    
    collectValues:function(valueCollector, prefix) {
    },
    
    getSubpipelines:function(outerValue, wrapperValue) {
       return null;
    }
}