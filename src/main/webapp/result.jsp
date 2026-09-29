<%-- 
    Document   : result
    Created on : 29 Sep 2026, 1:29:20 PM
    Author     : Anh Tuan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Kết Quả</title>
        <!-- Liên kết file main.css -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css" type="text/css"/>
    </head>
    <body>
        <div class="container">
            <h2>Thông Báo Kết Quả</h2>
            <div class="result-box">
                <p>${messageResult}</p>
            </div>
            <a href="index.jsp" class="back-link">&larr; Quay lại trang chủ</a>
        </div>
    </body>
</html>
