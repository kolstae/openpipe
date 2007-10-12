<%@ taglib prefix="trank" uri="http://trank.no/jsp/taglib"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"

%><c:set var="setupNameLocal" value="${setupName}" scope="page"
/><c:set var="setupTypeLocal" value="${setupType}" scope="page"
/><c:set var="setupValueLocal" value="${setupValue}" scope="page"

/><c:set var="setupName" value="${setupNameLocal}_kt" scope="request"
/><c:set var="setupType" value="${trank:getMapKeyType(setupTypeLocal)}" scope="request"
/><c:set var="setupValue" value="${null}" scope="request"
/><jsp:include page="/WEB-INF/javascript/setup/setup.jsp"

/><c:set var="setupType" value="${trank:getMapValueType(setupTypeLocal)}" scope="request"
/><c:set var="setupValue" value="${null}" scope="request"
/><c:set var="setupName" value="${setupNameLocal}_vt" scope="request"
/><jsp:include page="/WEB-INF/javascript/setup/setup.jsp"

/>${setupNameLocal} = new mapvalue.builder(${setupNameLocal}_kt, ${setupNameLocal}_vt);
<c:forEach items="${setupValueLocal}" var="mapPair" varStatus="ind"
   ><c:set var="setupName" value="${setupNameLocal}_ki" scope="request"
   /><c:set var="setupType" value="${trank:getMapKeyType(setupTypeLocal)}" scope="request"
   /><c:set var="setupValue" value="${mapPair.key}" scope="request"
   /><jsp:include page="/WEB-INF/javascript/setup/setup.jsp"

   /><c:set var="setupName" value="${setupNameLocal}_vi" scope="request"
   /><c:set var="setupType" value="${trank:getMapValueType(setupTypeLocal)}" scope="request"
   /><c:set var="setupValue" value="${mapPair.value}" scope="request"
   /><jsp:include page="/WEB-INF/javascript/setup/setup.jsp"
   
   />${setupNameLocal}.put(${setupNameLocal}_ki, ${setupNameLocal}_vi);
</c:forEach>
