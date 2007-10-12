<%-- field html class definitions --%>
var fieldHtml = new Object();

fieldHtml.builder = function() {
}

fieldHtml.builder.prototype = {

    update:function(vis) {
    },
    
    //buildScorerParams:function(scorerFactory, scorer, name, prefix) {
    //   var ret = '';
    //    <c:forEach items="${sources}" var="pageScopeSource">
    // 	    <c:set var="source" value="${pageScopeSource}" scope="request"/>
    // 		if(this.source.name == "${source.name}") {
    // 			<c:forEach items="${source.scorerFactories}" var="pageScopeScorerFactory">
    //               <c:set var="scorerFactory" value="${pageScopeScorerFactory}" scope="request"/>
    // 				if(scorerFactory.name == "${scorerFactory.name}") {
    // 				    var paramKey = '';
    //                    var paramValue = '';
    // 					<c:forEach items="${scorerFactory.params}" var="p">
    // 					    paramKey = (prefix == null ? '' : prefix + '.') + "<c:out value="${p}" escapeXml="true"/>";
    // 					    paramValue = scorer != null ? scorer.getParam(paramKey) : null;
    // 					    if(paramValue == null || paramValue.length == 0)
    //                            paramValue = "<c:out value="${scorerFactory.defaults[p]}" escapeXml="true"/>";
    //                        ret += '<td style="padding-left: 25px">';
    //                        <jsp:include page="/WEB-INF/javascript/render/scorerparam/${p}.jsp" />
    //                        ret += '</td>';
    // 					</c:forEach>
    // 				}
    // 			</c:forEach>
    // 		}
    // 	</c:forEach>
    //
    //  return ret;
    //}
}