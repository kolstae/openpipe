<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Frode Johannesen (T-Rank AS)
     step manager --%>
var StepManager = new Object();

StepManager.builder = function() {
   var stepInstance;

   this.steps = new Array();
   <c:forEach items="${defaultSteps}" var="oneStep" varStatus="stepInd"
      ><c:set var="setupName" value="_si" scope="request"
      /><c:set var="setupValue" value="${oneStep}" scope="request"
      /><c:set var="setupType" value="${oneStep.step.class}" scope="request"
      /><%@ include file="/WEB-INF/javascript/setup/setup.jsp"
      %>this.steps[${stepInd.index}] = _si;
   </c:forEach>

   this.configuredSteps = new Array();
   <c:forEach items="${configuredSteps}" var="oneStep" varStatus="stepInd"
      ><c:set var="setupName" value="_si" scope="request"
      /><c:set var="setupValue" value="${oneStep}" scope="request"
      /><c:set var="setupType" value="${oneStep.step.class}" scope="request"
      /><%@ include file="/WEB-INF/javascript/setup/setup.jsp"
      %>this.configuredSteps[${stepInd.index}] = _si;
   </c:forEach>
}

 
StepManager.builder.prototype = {
   getAvailableSteps:function() {
      return this.steps;
   },
    
   getConfiguredSteps:function() {
      return this.configuredSteps;
   },

   getDefaultNameFromClassName:function(className) {
      return this.getStepFromClassName(className).getField('name').getValue().getFieldValue();
   },
    
   getStepFromClassName:function(className) {
      for(var i = 0; i < this.steps.length; ++i) {
         if(className == this.steps[i].getClassName()) {
            return this.steps[i];
         }
      }
      return null;
   },
    
   toString:function() {
      return "StepManager";
   }
}