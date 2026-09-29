/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.assignment5.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.mycompany.assignment5.model.User;
import com.mycompany.assignment5.data.UserDB;
import com.mycompany.assignment5.data.MailUtilGmail;

/**
 *
 * @author Anh Tuan
 */
@WebServlet(name = "EmailListServlet", urlPatterns = {"/EmailListServlet"})
public class EmailListServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet EmailListServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EmailListServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển tiếp về trang chủ index.jsp khi người dùng truy cập bằng GET
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thiết lập mã hóa UTF-8 để nhận tiếng Việt không bị lỗi font
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 1. Nhận dữ liệu từ form index.jsp
        String action = request.getParameter("action");
        if (action == null) {
            action = "register";
        }

        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");

        if (email != null) {
            email = email.trim();
        }
        if (fullName != null) {
            fullName = fullName.trim();
        }

        // 2. Khởi tạo đối tượng User JavaBean
        User user = new User(fullName, email);

        // 3. Set thuộc tính user vào request scope để giữ lại form khi có lỗi
        request.setAttribute("user", user);

        String url = "/index.jsp";

        // 4. Phân nhánh xử lý theo hành động (Nút Đăng ký hoặc Nút Gửi Email)
        if ("register".equals(action)) {
            // === XỬ LÝ ĐĂNG KÝ MỚI & GỬI EMAIL NGAY ===
            if (email == null || email.isEmpty() || fullName == null || fullName.isEmpty()) {
                request.setAttribute("message", "Vui lòng nhập đầy đủ Họ và tên và Email để đăng ký!");
                url = "/index.jsp";
            } else if (UserDB.emailExists(email)) {
                request.setAttribute("message", "Email này đã tồn tại trong hệ thống. Bạn có thể bấm 'Gửi Email' để nhận lại thư!");
                url = "/index.jsp";
            } else {
                int result = UserDB.insert(user);
                if (result > 0) {
                    // Gửi email xác nhận ngay khi đăng ký thành công
                    try {
                        sendConfirmationEmail(user);
                        request.setAttribute("user", user);
                        url = "/thanks.jsp";
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("message", "Đã lưu thông tin nhưng gửi email thất bại: " + e.getMessage());
                        url = "/index.jsp";
                    }
                } else {
                    request.setAttribute("message", "Có lỗi xảy ra khi lưu vào cơ sở dữ liệu. Vui lòng thử lại sau!");
                    url = "/index.jsp";
                }
            }
        } else if ("send_mail".equals(action)) {
            // === XỬ LÝ GỬI EMAIL CHO TÀI KHOẢN ĐÃ ĐĂNG KÝ ===
            if (email == null || email.isEmpty()) {
                request.setAttribute("message", "Vui lòng nhập địa chỉ Email cần gửi thư!");
                url = "/index.jsp";
            } else if (!UserDB.emailExists(email)) {
                request.setAttribute("message", "Email này chưa đăng ký trong hệ thống. Vui lòng nhập Họ tên và bấm 'Đăng Ký' trước!");
                url = "/index.jsp";
            } else {
                // Lấy thông tin họ tên từ Database nếu người dùng không nhập lại họ tên
                User existingUser = UserDB.selectUser(email);
                if (existingUser != null) {
                    if (fullName == null || fullName.isEmpty()) {
                        user = existingUser;
                    }
                }

                // Gửi email xác nhận
                try {
                    sendConfirmationEmail(user);
                    request.setAttribute("user", user);
                    url = "/thanks.jsp";
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("message", "Gửi email thất bại: " + e.getMessage());
                    url = "/index.jsp";
                }
            }
        }
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }

    /**
     * Hàm phụ trợ gửi email xác nhận với mẫu Dear client và thông tin người dùng
     */
    private void sendConfirmationEmail(User user) throws Exception {
        String to = user.getEmail();
        String envFrom = System.getenv("GMAIL_USERNAME");
        String from = (envFrom != null && !envFrom.trim().isEmpty()) ? envFrom.trim() : "hlat10901@gmail.com";
        String subject = "Xác nhận đăng ký & Thông tin tài khoản - " + user.getFullName();

        String body = "<div style=\"font-family: Arial, Helvetica, sans-serif; line-height: 1.6; color: #333333; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden;\">"
                    + "  <div style=\"background-color: #3182ce; color: #ffffff; padding: 20px; text-align: center;\">"
                    + "    <h2 style=\"margin: 0; font-size: 22px;\">Thông Báo Xác Nhận Đăng Ký</h2>"
                    + "  </div>"
                    + "  <div style=\"padding: 25px;\">"
                    + "    <p style=\"font-size: 16px;\"><strong>Dear " + user.getFullName() + ",</strong></p>"
                    + "    <p>Cảm ơn bạn đã tham gia và đồng hành cùng hệ thống của chúng tôi.</p>"
                    + "    <p>Dưới đây là thông tin chi tiết bạn đã cung cấp khi gửi yêu cầu:</p>"
                    + "    <div style=\"background-color: #f7fafc; border-left: 4px solid #3182ce; padding: 15px; margin: 20px 0; border-radius: 4px;\">"
                    + "      <p style=\"margin: 6px 0;\"><strong>Họ và tên:</strong> " + user.getFullName() + "</p>"
                    + "      <p style=\"margin: 6px 0;\"><strong>Địa chỉ Email:</strong> " + user.getEmail() + "</p>"
                    + "    </div>"
                    + "    <p>Thông tin của bạn đã được ghi nhận an toàn trên cơ sở dữ liệu. Chúng tôi sẽ cập nhật các thông tin hữu ích nhất đến bạn.</p>"
                    + "    <p style=\"margin-top: 25px;\">Một lần nữa, xin chân thành cảm ơn bạn và chúc bạn một ngày làm việc tuyệt vời!</p>"
                    + "    <p style=\"margin-bottom: 0;\"><strong>Trân trọng,</strong><br/>Ban Quản Trị Hệ Thống</p>"
                    + "  </div>"
                    + "  <div style=\"background-color: #edf2f7; color: #718096; font-size: 12px; padding: 12px; text-align: center;\">"
                    + "    Email này được gửi tự động từ ứng dụng Java Web Servlet & JSP."
                    + "  </div>"
                    + "</div>";

        MailUtilGmail.sendMail(to, from, subject, body, true);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
