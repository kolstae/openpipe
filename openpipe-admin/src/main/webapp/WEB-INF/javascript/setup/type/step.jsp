<%@ taglib prefix="trank" uri="http://trank.no/jsp/taglib"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"

%><c:set var="setupNameLocal" value="${setupName}" scope="page"
/><c:set var="setupTypeLocal" value="${setupValue.step.class}" scope="page"
/><c:set var="setupValueLocal" value="${setupValue}" scope="page"

/>${setupNameLocal} = new step.builder('${setupValueLocal.step.class.name}');

<c:forEach items="${setupValueLocal.fields}" var="field" varStatus="ind"
   ><c:set var="setupName" value="${setupNameLocal}_f" scope="request"
   /><c:set var="setupType" value="${field.value}" scope="request"
   /><c:set var="setupValue" value="${setupValueLocal.values[field.key]}" scope="request"
   /><jsp:include page="/WEB-INF/javascript/setup/setup.jsp"

   />${setupNameLocal}.addField(new field.builder('${field.key}', ${setupNameLocal}_f));
</c:forEach>