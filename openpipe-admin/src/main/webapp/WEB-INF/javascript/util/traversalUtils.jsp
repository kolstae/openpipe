<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Frode Johannesen (T-Rank AS)
     traversal utils --%>
var TraversalUtils = new Object();

TraversalUtils.builder = function() {
}
 
TraversalUtils.builder.prototype = {
   traverseDepthFirst:function(startArray, startCallback, endCallback, prefix) {
      var prefixDot = prefix == null || prefix.length == 0 ? "" : prefix + ".";
      var stack = [ [ ] ];
      for(var i = 0; i < startArray.length; ++i) { <%-- make a copy for safe splicing --%>
         stack[0][stack[0].length] = startArray[i];
      }

      var work = [ ];
      var names = [ prefix ];
      var index = [ -1 ];
      var level = 0;
      while(level != -1) {
         var current = null;
         var currentIndex = 0;
         level = -1;
         for(var sti = stack.length-1; current == null && sti >= 0; --sti) {
            if(stack[sti].length > 0) {
               current = stack[sti][0];
               currentIndex = ++index[sti];

               var post = "";
               if(sti > 0 && objectUtils.isEntryValue(work[sti-1])) {
                  post = (currentIndex == 0 ? 'key' : 'value') + "." + (index[sti-1]);
               }
               else if(sti == 0 || objectUtils.isListValue(work[sti-1])) {
                  post = "" + currentIndex;
               }
               
               stack[sti].splice(0, 1);

               var postdot = post.length == 0 ? post : post + ".";
               var pre = sti == 0 ? prefix : names[sti-1];
               
               if(objectUtils.isField(current)) {
                  post = postdot + current.getName();
               }
               
               if(pre.length == 0) {
                  names[sti] = post;
               }
               else {
                  names[sti] = post.length == 0 ? pre : pre + "." + post;
               }

               work[sti] = current;
               level = sti;
            }
         }
         
         for(var wi = work.length-1; wi > level; --wi) {
            if(endCallback != null) {
               endCallback(work[wi], wi);
            }
            work.splice(wi, 1);
         }
         
         if(current != null) {
            if(startCallback(current, names[level], level)) {
               work[level] = current;
               var tmp = current.getChildValues();
               if(tmp != null) {
                  stack[level+1] = objectUtils.copyArray(tmp); <%-- make a copy for safe splicing --%>
                  index[level+1] = -1;
               }
            }
         }
      }
   }
}