<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>RSS-Storage</title>
    <link rel="icon" type="image/png" href="<spring:url value="/static/favicon.png" />" />
    <link rel="stylesheet" type="text/css" href="http://yandex.st/bootstrap/3.1.1/css/bootstrap.min.css"/>
</head>
<body>

<div class="container">
    <div class="col-md-8 col-md-offset-2">
        <h1>RSS-Storage</h1>

        <h3>Конференция Радио Уткин</h3>
        <a href="<spring:url value="/radioutkin/"/>">html</a> | <a href="<spring:url value="/radioutkin/rss/" />">rss</a> | <a href="http://radioutkin.ru/conference/">оригинал</a>

        <h3>Рецензии RollingStone.ru</h3>
        <a href="<spring:url value="/rolling-stone/" />">html</a> | <a href="<spring:url value="/rolling-stone/rss/" />">rss</a> | <a href="http://rollingstone.ru/reviews">оригинал</a>

        <h3>Новое на сайте It-Students</h3>
        <a href="<spring:url value="/it-students/" />">html</a> | <a href="<spring:url value="/it-students/rss/" />">rss</a> | <a href="http://it-students.net/">оригинал</a>

        <h3>Новоcти сериалов от LostFilm.tv</h3>
        <a href="<spring:url value="/lostfilm/" />">html</a> | <a href="<spring:url value="/lostfilm/rss/" />">rss</a> | <a href="http://www.lostfilm.tv/">оригинал</a>
    </div>
</div>

</body>
</html>
