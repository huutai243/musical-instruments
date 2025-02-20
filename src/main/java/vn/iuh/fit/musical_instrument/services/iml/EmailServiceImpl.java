package vn.iuh.fit.musical_instrument.services.iml;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.iuh.fit.musical_instrument.services.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            // Tạo đối tượng MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // Sử dụng MimeMessageHelper để cài đặt 'to', 'subject', 'text' (HTML/Text)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);

            // Gửi nội dung HTML (đặt true). Nếu muốn text thuần, dùng false.
            helper.setText(body, true);

            // Thực hiện gửi email qua SMTP Gmail
            mailSender.send(mimeMessage);

            System.out.println("Email sent to: " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
            // Bạn có thể ném RuntimeException hoặc custom exception tùy yêu cầu
        }
    }

    /*
    // Nếu muốn gửi email có template HTML (Thymeleaf),
    // có thể tạo thêm hàm gửi xác thực (sendVerificationEmail) kèm TemplateEngine.
    // Ví dụ:
    //
    // public void sendVerificationEmail(String to, String userName, String verificationLink) {
    //     try {
    //         MimeMessage mimeMessage = mailSender.createMimeMessage();
    //         MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    //         helper.setTo(to);
    //         helper.setSubject("Account Verification");
    //
    //         // Render template 'verify-email.html' bằng Thymeleaf
    //         // (nếu đã inject TemplateEngine):
    //         // Context context = new Context();
    //         // context.setVariable("username", userName);
    //         // context.setVariable("verificationLink", verificationLink);
    //         // String htmlContent = templateEngine.process("verify-email", context);
    //         // helper.setText(htmlContent, true);
    //
    //         mailSender.send(mimeMessage);
    //     } catch (MessagingException e) {
    //         e.printStackTrace();
    //     }
    // }
    */
}
