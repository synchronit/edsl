<%@ attribute name="nextTokens" required="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fqlTags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <c:forEach var="token" items="${nextTokens}">
        <c:set var="graphsHTML" value="${graphsHTML}<br/>&rarr;${token.string}" />
		<c:if test="${!empty token.nextTokens}">
			<fqlTags:bnfTokenGraph nextTokens="${bnfToken.nextTokens}" />			
		</c:if>
    </c:forEach>

