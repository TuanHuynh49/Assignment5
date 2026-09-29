<%-- 
    Document   : thanks
    Created on : 29 Sep 2026, 1:41:17 PM
    Author     : Anh Tuan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Cảm ơn đã đăng ký</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css" type="text/css"/>
    </head>
    <body>
        <div class="container">
            <h2>Cảm Ơn Bạn Đã Tham Gia!</h2>
            <p>Dưới đây là thông tin bạn vừa cung cấp:</p>

            <div class="result-box">
                <p><strong>Họ và tên:</strong> ${user.fullName}</p>
                <p><strong>Địa chỉ Email:</strong> ${user.email}</p>
            </div>

            <p style="font-size: 14px; color: #718096; margin-top: 15px;">
                Thông tin của bạn đã được ghi nhận vào hệ thống thành công.
            </p>

            <br/>
            <a href="index.jsp" class="back-link">&larr; Quay lại trang chủ</a>
        </div>
    </body>
</html>
