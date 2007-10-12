<%@ taglib prefix="trank" uri="http://trank.no/jsp/taglib"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" 
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"

%><c:set var="primitiveType" value="${trank:getPrimitiveTypeName(setupType)}"
/><c:choose
   ><c:when test="${trank:isStringType(setupType)}"
      >${setupName} = new basicvalue.builder('string', '<c:out value="${setupValue}" escapeXml="true"/>');
   </c:when
   ><c:when test="${primitiveType == 'boolean'}"
      >${setupName} = new basicvalue.builder('boolean', <c:out value="${setupValue != null ? setupValue : false}" escapeXml="true"/>);
   </c:when
   ><c:when test="${setupValue == null}"
      >${setupName} = new basicvalue.builder('${primitiveType}', 0);
   </c:when
      ><c:otherwise
      >${setupName} = new basicvalue.builder('${primitiveType}', ${setupValue});
   </c:otherwise
></c:choose>