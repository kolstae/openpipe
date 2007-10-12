<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="trank" uri="http://trank.no/jsp/taglib" %>

<html>
<head>
  <script type="text/javascript">
    <%@ include file="/WEB-INF/javascript/model/field.jsp" %>
    
    <%@ include file="/WEB-INF/javascript/model/value/basicvalue.jsp"
    %><%@ include file="/WEB-INF/javascript/model/value/listvalue.jsp"
    %><%@ include file="/WEB-INF/javascript/model/value/mapvalue.jsp"
    %><%@ include file="/WEB-INF/javascript/model/value/stepvalue.jsp"
    %><%@ include file="/WEB-INF/javascript/model/value/errorvalue.jsp"

    %><%@ include file="/WEB-INF/javascript/model/pipeline/mainpipeline.jsp"
    %><%@ include file="/WEB-INF/javascript/model/pipeline/listpipeline.jsp"
    %><%@ include file="/WEB-INF/javascript/model/pipeline/singlesteppipeline.jsp"
    
    %><%@ include file="/WEB-INF/javascript/model/step.jsp" %>
    <%@ include file="/WEB-INF/javascript/model/pipeline.jsp" %>
    <%@ include file="/WEB-INF/javascript/model/subpipeline.jsp"
     
    %><%@ include file="/WEB-INF/javascript/render/fieldHtml.jsp"

    %><%@ include file="/WEB-INF/javascript/util/domUtils.jsp"
    %><%@ include file="/WEB-INF/javascript/util/objectUtils.jsp"
    %><%@ include file="/WEB-INF/javascript/util/traversalUtils.jsp"

    %><%@ include file="/WEB-INF/javascript/stepManager.jsp"
    %><%@ include file="/WEB-INF/javascript/htmlManager.jsp"
    %><%@ include file="/WEB-INF/javascript/updateManager.jsp"
    %><%@ include file="/WEB-INF/javascript/pipelineManager.jsp"
    %>

    var domUtils = new DomUtils.builder();
    var objectUtils = new ObjectUtils.builder();
    var traversalUtils = new TraversalUtils.builder();
    
    var stepManager = new StepManager.builder();
    var htmlManager = new HtmlManager.builder();
    var updateManager = new UpdateManager.builder();
    var pipelineManager = new PipelineManager.builder();
    
    function handleStepClick(className) {
       var step = stepManager.getStepFromClassName(className);

       var name = prompt("Name", step.getField('name').getValue().getFieldValue());
       if(typeof name == 'string') {
          while(name.substring(0, 1) == ' ') name = name.substring(1, name.length);
          while(name.substring(name.length-1, name.length) == ' ') name = name.substring(0, name.length-1);
       }
       
       if(typeof name == 'string' && name.length > 0) {
          step = step.getClone();
          valueInstance = new basicvalue.builder('string', name);
          fieldInstance = new field.builder('name', valueInstance);
          step.addField(fieldInstance);
          pipelineManager.addStep(step);
          pipelineManager.setWorkingStep(step);
          htmlManager.renderSelectedSteps('selectedSteps');
          htmlManager.renderWorkingStep('workingStep');

//          workingPipeline.setWorkingStep(step, "edit.step");
//          document.getElementById('workingStep').innerHTML = workingPipeline.getWorkingStepHtml();
//          document.getElementById('selectedSteps').innerHTML = workingPipeline.getHtml();
       }
    }
    
    function switchWorkingStep(pipelineIndex, index) {
       if(pipelineManager.setWorkingStepFromIndex(pipelineIndex)) {
          htmlManager.renderSelectedSteps('selectedSteps');
          htmlManager.renderWorkingStep('workingStep');
       }
    }
    
    function moveWorkingStep(up) {
       if(workingPipeline.moveWorkingStep(up)) {
          document.getElementById('workingStep').innerHTML = workingPipeline.getWorkingStepHtml();
          document.getElementById('selectedSteps').innerHTML = workingPipeline.getHtml();
       }
    }
    
    function init() {
//       workingPipeline.setWorkingStepFromIndex(0, ${fn:length(param.workingStepIndex) > 0 ? param.workingStepIndex : 0});
       htmlManager.renderAvailableSteps('availableSteps');
       htmlManager.renderSelectedSteps('selectedSteps');
       htmlManager.renderWorkingStep('workingStep');
//       document.getElementById('workingStep').innerHTML = workingPipeline.getWorkingStepHtml();
//       workingPipeline.updateWorkingStep();
//       document.getElementById('workingStep').innerHTML = workingPipeline.getWorkingStepHtml();
//       document.getElementById('availableSteps').innerHTML = stepManager.toString();
//       document.getElementById('selectedSteps').innerHTML = workingPipeline.getHtml();
    }

    function moveWorkingStepToPipeline(pipelineIndex) {
       if(pipelineIndex == '.') return;
       workingPipeline.moveWorkingStepToPipeline(pipelineIndex);
       document.getElementById('workingStep').innerHTML = workingPipeline.getWorkingStepHtml();
       document.getElementById('availableSteps').innerHTML = stepManager.toString();
       document.getElementById('selectedSteps').innerHTML = workingPipeline.getHtml();
    }
   </script>
</head>
<body onload="init();">
       <table>
         <tr>
           <td width="180" id="availableSteps" valign="top">
              &nbsp;
           </td>
           <td width="180" id="selectedSteps" valign="top">
              &nbsp;
           </td>
           <td valign="top">
             <div id="workingStep"> &nbsp; </div>
           </td>
         </tr>
       </table>
       <form action="admin.form" method="get" id="postformdiv">
       </form>
       <input type="button" value="Save pipeline" onclick="javascript:updateManager.postWorkingPipeline()"/>	
       <input type="button" value="Debug" onclick="javascript:updateManager.debugPostPipeline()"/>	
       <div id="debug"> &nbsp; </div>
    </div>
</body>

</html>