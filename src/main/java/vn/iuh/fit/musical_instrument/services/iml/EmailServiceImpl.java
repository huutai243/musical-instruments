package vn.iuh.fit.musical_instrument.services.iml;

import org.springframework.stereotype.Service;
import vn.iuh.fit.musical_instrument.services.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        // Ở môi trường thực tế, sử dụng JavaMailSender hoặc dịch vụ email bên ngoài
        // Ở đây, chúng ta chỉ in ra console để mô phỏng việc gửi email.
        System.out.println("Sending email to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
    }
}

