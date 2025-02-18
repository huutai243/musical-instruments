package vn.iuh.fit.musical_instrument.services;


public interface EmailService {
    void sendEmail(String to, String subject, String body);
}

