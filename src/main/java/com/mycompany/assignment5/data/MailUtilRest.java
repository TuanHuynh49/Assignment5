/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.assignment5.data;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Utility gửi email qua HTTP REST API (HTTPS Cổng 443)
 * Giải pháp tối ưu khi deploy ứng dụng lên các nền tảng Cloud như Render
 * (nơi các cổng SMTP 25, 465, 587 bị chặn).
 * 
 * @author Anh Tuan
 */
public class MailUtilRest {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Gửi Email thông qua Brevo (Sendinblue) REST API v3
     * Endpoint: https://api.brevo.com/v3/smtp/email
     */
    public static boolean sendMailViaBrevo(String apiKey, String toEmail, String toName, String fromEmail, String fromName, String subject, String htmlContent) throws Exception {
        String endpoint = "https://api.brevo.com/v3/smtp/email";
        
        String senderEmail = (fromEmail != null && fromEmail.contains("@")) ? fromEmail : "hlat10901@gmail.com";
        String senderName = (fromName != null && !fromName.isEmpty()) ? fromName : "Hệ Thống Đăng Ký";
        String recipientName = (toName != null && !toName.isEmpty()) ? toName : toEmail;

        String jsonPayload = "{"
                + "\"sender\":{\"name\":\"" + escapeJson(senderName) + "\",\"email\":\"" + escapeJson(senderEmail) + "\"},"
                + "\"to\":[{\"email\":\"" + escapeJson(toEmail) + "\",\"name\":\"" + escapeJson(recipientName) + "\"}],"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"htmlContent\":\"" + escapeJson(htmlContent) + "\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(15))
                .header("accept", "application/json")
                .header("api-key", apiKey.trim())
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println(">> [Brevo REST API] Đã gửi email thành công tới: " + toEmail + " | Response: " + response.body());
            return true;
        } else {
            System.err.println(">> [Brevo REST API Error] Status: " + response.statusCode() + " | Body: " + response.body());
            throw new RuntimeException("Brevo API lỗi (" + response.statusCode() + "): " + response.body());
        }
    }

    /**
     * Gửi Email thông qua Resend REST API
     * Endpoint: https://api.resend.com/emails
     */
    public static boolean sendMailViaResend(String apiKey, String toEmail, String fromEmail, String subject, String htmlContent) throws Exception {
        String endpoint = "https://api.resend.com/emails";
        
        // Mặc định Resend cho tài khoản chưa xác thực domain dùng "onboarding@resend.dev"
        String from = (fromEmail != null && fromEmail.endsWith("@resend.dev")) ? fromEmail : "onboarding@resend.dev";

        String jsonPayload = "{"
                + "\"from\":\"" + escapeJson(from) + "\","
                + "\"to\":[\"" + escapeJson(toEmail) + "\"],"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"html\":\"" + escapeJson(htmlContent) + "\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + apiKey.trim())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println(">> [Resend REST API] Đã gửi email thành công tới: " + toEmail + " | Response: " + response.body());
            return true;
        } else {
            System.err.println(">> [Resend REST API Error] Status: " + response.statusCode() + " | Body: " + response.body());
            throw new RuntimeException("Resend API lỗi (" + response.statusCode() + "): " + response.body());
        }
    }

    /**
     * Tự động phát hiện phương thức gửi:
     * 1. Nếu có BREVO_API_KEY -> Dùng Brevo REST API (Cổng HTTPS 443)
     * 2. Nếu có RESEND_API_KEY -> Dùng Resend REST API (Cổng HTTPS 443)
     * 3. Nếu có MAIL_API_KEY -> Tự phân tích key và gửi REST
     * 4. Fallback: Dùng JavaMail SMTP Gmail (thích hợp chạy localhost)
     */
    public static void sendMail(String toEmail, String toName, String fromEmail, String subject, String htmlContent) throws Exception {
        String brevoKey = System.getenv("BREVO_API_KEY");
        String resendKey = System.getenv("RESEND_API_KEY");
        String generalApiKey = System.getenv("MAIL_API_KEY");

        if (brevoKey != null && !brevoKey.trim().isEmpty()) {
            sendMailViaBrevo(brevoKey, toEmail, toName, fromEmail, "Hệ Thống Đăng Ký", subject, htmlContent);
        } else if (resendKey != null && !resendKey.trim().isEmpty()) {
            sendMailViaResend(resendKey, toEmail, fromEmail, subject, htmlContent);
        } else if (generalApiKey != null && !generalApiKey.trim().isEmpty()) {
            if (generalApiKey.trim().startsWith("re_")) {
                sendMailViaResend(generalApiKey, toEmail, fromEmail, subject, htmlContent);
            } else {
                sendMailViaBrevo(generalApiKey, toEmail, toName, fromEmail, "Hệ Thống Đăng Ký", subject, htmlContent);
            }
        } else {
            // Chạy mặc định qua JavaMail SMTP (hoặc khi chạy ở local)
            MailUtilGmail.sendMail(toEmail, fromEmail, subject, htmlContent, true);
        }
    }

    /**
     * Escape chuỗi sang định dạng JSON an toàn
     */
    private static String escapeJson(String raw) {
        if (raw == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        String hex = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - hex.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
