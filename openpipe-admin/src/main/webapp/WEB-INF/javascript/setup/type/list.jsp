<%@ taglib prefix="trank" uri="http://trank.no/jsp/taglib"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"

%><c:set var="setupNameLocal" value="${setupName}" scope="page"
/><c:set var="setupTypeLocal" value="${setupType}" scope="page"
/><c:set var="setupValueLocal" value="${setupValue}" scope="page"

/><c:set var="setupName" value="${setupNameLocal}_lt" scope="request"
/><c:set var="setupType" value="${trank:getListType(setupTypeLocal)}" scope="request"
/><c:set var="setupValue" value="${null}" scope="request"
/><jsp:include page="/WEB-INF/javascript/setup/setup.jsp"

/>${setupNameLocal}_list = new Array();
<c:forEach items="${setupValueLocal}" var="listItem" varStatus="ind"
   ><c:set var="setupName" value="${setupNameLocal}_li" scope="request"
   /><c:set var="setupType" value="${trank:getListType(setupTypeLocal)}" scope="request"
   /><c:set var="setupValue" value="${listItem}" scope="request"
   /><jsp:include page="/WEB-INF/javascript/setup/setup.jsp"

   />${setupNameLocal}_list[${ind.index}] = ${setupNameLocal}_li;
</c:forEach>
${setupNameLocal} = new listvalue.builder(${setupNameLocal}_lt, ${setupNameLocal}_list);