<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Рецензии RollingStone.Ru</title>
    <link rel="icon" type="image/ico" href="<c:url value="/static/favicons/rs.ico" />" />
    <link rel="stylesheet" type="text/css" href="http://yandex.st/bootstrap/3.1.1/css/bootstrap.min.css"/>
</head>
<body>

<div class="container">
    <div class="col-md-8 col-md-offset-2">
        <h2 class="page-header">Рецензии RollingStone.Ru</h2>
        <c:forEach items="${objects}" var="rev">
            <div class="text-justify">
                <h4><a href="${rev.url}">${rev.title}</a>
                    <small class="pull-right">${rev.author}, <fmt:formatDate value="${rev.date}" pattern="dd.MM.yyyy" /></small>
                </h4>
                ${rev.text}
            </div>
            <hr/>
        </c:forEach>
    </div>
</div>

</body>
</html>
