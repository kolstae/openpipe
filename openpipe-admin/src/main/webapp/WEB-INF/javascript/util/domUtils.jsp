<%-- Frode Johannesen (T-Rank AS)
     dom utils --%>
var DomUtils = new Object();

DomUtils.builder = function() {
   this.debug = false;
}
 
DomUtils.builder.prototype = {
   addTag:function(parent, tag, attrs, content) {
      if(this.debug) {
         alert(parent + " " + tag + " " + attrs + " " + content);
      }
   
      var ret = null;
      
      try {
         var v = "<" + tag;
         if(attrs != null) {
            for(var key in attrs) {
               v += ' ' + key + '="' + attrs[key] + '"';
            }
         }
         v += " />";

         ret = document.createElement(v); <%-- IE --%>
      } catch (e) {                                                                                                                                                  
         ret = document.createElement(tag); <%--non IE --%>
         if(attrs != null) {
            for(var key in attrs) {
               ret.setAttribute(key, attrs[key]);
            }
         }
      }
      
      this.addText(ret, content);
      
      if(parent != null) {
         parent.appendChild(ret);
      }
      
      return ret;
   },

   addText:function(parent, content) {
      var ret = null;
      if(content != null && content.length > 0) {
         ret = document.createTextNode(content);
         parent.appendChild(ret);
      }
   }
}