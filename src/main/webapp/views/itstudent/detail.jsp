<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Новое на сайте It-Students.net</title>
    <link rel="icon" type="image/png" href="<c:url value="/static/favicons/itstudents.ico" />" />
    <link rel="stylesheet" type="text/css" href="http://yandex.st/bootstrap/3.1.1/css/bootstrap.min.css"/>
</head>
<body>

<div class="container">
    <div class="col-md-8 col-md-offset-2">
        <h2 class="page-header">Новое на сайте It-Students.net</h2>
        <c:forEach items="${objects}" var="post">
            <div class="qa">
                <h3><a href="${post.url}">${post.title}</a></h3>
                <p>${post.text}</p>
            </div>
            <hr/>
        </c:forEach>
    </div>
</div>

</body>
</html>
