/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.assignment5.data;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
/**
 *
 * @author Anh Tuan
 */
public class MailUtilGmail {

    public static void sendMail(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        // 1. Cấu hình các thuộc tính kết nối SMTP Gmail[cite: 1]
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", "smtp.gmail.com");
        props.put("mail.smtps.port", "465");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");
        props.put("mail.smtps.ssl.enable", "true");
        props.put("mail.smtps.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtps.socketFactory.port", "465");
        props.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtps.socketFactory.fallback", "false");

        // Loại bỏ khoảng trắng trong App Password (bắt buộc đối với Google SMTP)
        final String username = "hlat10901@gmail.com";
        final String appPassword = "cjwn opbt qtmj zgza".replaceAll("\\s+", "");

        // 2. Tạo Session xác thực[cite: 1]
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });
        session.setDebug(true); // Bật log ở output window để dễ theo dõi tiến trình gửi

        // 3. Tạo thông điệp thư[cite: 1]
        MimeMessage message = new MimeMessage(session);
        // Thiết lập UTF-8 cho Subject để tránh lỗi font tiếng Việt
        message.setSubject(subject, "UTF-8");
        
        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body, "UTF-8");
        }

        // 4. Địa chỉ người gửi và người nhận[cite: 1]
        Address fromAddress;
        try {
            fromAddress = new InternetAddress(from, "Hệ Thống Đăng Ký", "UTF-8");
        } catch (Exception e) {
            fromAddress = new InternetAddress(from);
        }
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        // 5. Mở kết nối và gửi thư[cite: 1]
        Transport transport = session.getTransport("smtps");
        try {
            transport.connect("smtp.gmail.com", 465, username, appPassword);
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println(">> Đã gửi email thành công tới: " + to);
        } finally {
            transport.close();
        }
    }

    /**
     * Gửi email bất đồng bộ (Non-blocking Servlet Thread)
     */
    public static void sendMailAsync(String to, String from, String subject, String body, boolean bodyIsHTML) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        CompletableFuture.runAsync(() -> {
            Thread.currentThread().setContextClassLoader(cl);
            try {
                sendMail(to, from, subject, body, bodyIsHTML);
                System.out.println(">> [Async Mail] Đã gửi email thành công tới: " + to);
            } catch (Exception e) {
                System.err.println(">> [Async Mail Error] Gửi mail thất bại: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Hàm main để chạy kiểm tra trực tiếp (Run File Shift+F6) trên NetBeans
     */
    public static void main(String[] args) {
        String testTo = "hlat10901@gmail.com";
        String testFrom = "hlat10901@gmail.com";
        String testSubject = "Kiểm tra gửi email từ Java Web";
        String testBody = "<h3>Xin chào!</h3><p>Đây là email test gửi thành công từ hệ thống Java Web.</p>";

        try {
            System.out.println("--- BẮT ĐẦU GỬI MAIL TEST ---");
            sendMail(testTo, testFrom, testSubject, testBody, true);
            System.out.println("--- GỬI THÀNH CÔNG! HÃY KIỂM TRA HỘP THƯ (INBOX / SPAM) ---");
        } catch (Exception e) {
            System.err.println("--- GỬI THẤT BẠI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
