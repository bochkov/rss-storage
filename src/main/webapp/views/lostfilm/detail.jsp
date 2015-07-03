<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Новости сериалов от LostFilm.TV</title>
    <link rel="icon" type="image/png" href="<c:url value="/static/favicons/lostfilm.ico" />" />
    <link rel="stylesheet" type="text/css" href="http://yandex.st/bootstrap/3.1.1/css/bootstrap.min.css"/>
</head>
<body>

<div class="container">
    <div class="col-md-8 col-md-offset-2">
        <h2 class="page-header">Новости сериалов от LostFilm.TV</h2>
        <c:forEach items="${objects}" var="news">
            <div class="row">
                <h3><a href="${news.url}">${news.title}</a></h3>
                <div class="col-md-3">
                    <img src="${news.imgUrl}"/>
                </div>
                <div class="col-md-9 text-justify">
                    <p>${news.text}</p>
                    <small><em><fmt:formatDate value="${news.date}" pattern="HH:mm dd.MM.yyyy" /></em></small>
                </div>
                <hr/>
            </div>
        </c:forEach>
    </div>
</div>

</body>
</html>
