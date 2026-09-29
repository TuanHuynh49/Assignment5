<%-- 
    Document   : index
    Created on : 29 Sep 2026, 1:28:11 PM
    Author     : Anh Tuan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Gửi Email với JavaMail</title>
        <!-- Liên kết file main.css -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css" type="text/css"/>
    </head>
    <body>
        <div class="container">
            <h2>Đăng ký thành viên</h2>
            <form action="EmailListServlet" method="post">
                <input type="hidden" name="action" value="register" />
                <div class="form-group">
                    <label for="email">Địa chỉ email:</label>
                    <input type="email" id="email" name="email" value="${user.email}" placeholder="example@gmail.com" required />
                </div>
                <div class="form-group">
                    <label for="fullName">Họ và tên:</label>
                    <input type="text" id="fullName" name="fullName" value="${user.fullName}" placeholder="Nhập tên của bạn" required />
                </div>
                <div class="btn-group">
                    <button type="submit" class="btn-register">Đăng ký</button>
                </div>
            </form>
            
            <%-- Thông báo lỗi nếu có --%>
            <p style="color: red; font-style: italic; margin-top: 15px;">${message}</p>
        </div>
    </body>
</html>
