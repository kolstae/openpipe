<%@ taglib prefix="trank" uri="http://trank.no/jsp/taglib"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><c:choose
   ><c:when test="${setupType == null}">
         ${setupName} = new basicvalue.builder('string', 'missing type');
   </c:when
   ><c:when test="${trank:isStepType(setupType)}"
      ><jsp:include page="/WEB-INF/javascript/setup/type/step.jsp"
   /></c:when
   ><c:when test="${trank:isStringType(setupType) || fn:length(trank:getPrimitiveTypeName(setupType)) > 0}"
      ><jsp:include page="/WEB-INF/javascript/setup/type/basic.jsp"
   /></c:when
   ><c:when test="${trank:getListType(setupType) != null}"
      ><jsp:include page="/WEB-INF/javascript/setup/type/list.jsp"
   /></c:when
   ><c:when test="${trank:getMapKeyType(setupType) != null}"
      ><jsp:include page="/WEB-INF/javascript/setup/type/map.jsp"
   /></c:when
   ><c:otherwise
      >${setupName} = new errorvalue.builder('Not handled: ${setupType}');
   </c:otherwise
></c:choose>