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
                Thông tin của bạn đã được ghi nhận vào hệ thống thành công. Bạn có thể nhấn nút dưới đây để nhận email xác nhận.
            </p>

            <%-- Form gửi email từ trang cảm ơn --%>
            <form action="EmailListServlet" method="post" style="margin-top: 20px;">
                <input type="hidden" name="action" value="send_mail" />
                <input type="hidden" name="email" value="${user.email}" />
                <input type="hidden" name="fullName" value="${user.fullName}" />
                <button type="submit" class="btn-send">Gửi Email Xác Nhận</button>
            </form>

            <%-- Thông báo kết quả gửi email --%>
            <p style="color: #2e7d32; font-weight: bold; margin-top: 15px;">${successMessage}</p>
            <p style="color: red; font-style: italic; margin-top: 15px;">${message}</p>
            <p style="color: #3182ce; font-style: italic; margin-top: 15px;">${infoMessage}</p>

            <br/>
            <a href="index.jsp" class="back-link">&larr; Quay lại trang chủ</a>
        </div>
    </body>
</html>
