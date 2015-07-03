<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Конференция Радио Уткин</title>
    <link rel="icon" type="image/png" href="<c:url value="/static/favicons/radioutkin.png" />" />
    <link rel="stylesheet" type="text/css" href="http://yandex.st/bootstrap/3.1.1/css/bootstrap.min.css"/>
</head>
<body>

<div class="container">
    <div class="col-md-8 col-md-offset-2">
        <h2 class="page-header">Конференция Радио Уткин</h2>
        <c:forEach items="${objects}" var="qa">
            <div class="qa">
                <h3>
                    <c:choose>
                        <c:when test="${qa.link}">
                            <a href="${qa.link}">Вопрос # ${qa.id}</a>
                        </c:when>
                        <c:otherwise>
                            Вопрос задает ${qa.q_author}
                        </c:otherwise>
                    </c:choose>
                </h3>
                <div class="q">
                    <blockquote>
                        <p>${qa.q_text}</p>
                        <footer>${qa.q_author}, <em><fmt:formatDate value="${qa.published}" pattern="HH:mm dd.MM.yyyy" /></em></footer>
                    </blockquote>
                </div>
                <div class="a">
                    <blockquote class="blockquote-reverse">
                        <p>${qa.a_text}</p>
                        <footer>${qa.a_author}, <em><fmt:formatDate value="${qa.updated}" pattern="HH:mm dd.MM.yyyy" /></em></footer>
                    </blockquote>
                </div>
            </div>
            <hr/>
        </c:forEach>
    </div>
</div>

</body>
</html>
