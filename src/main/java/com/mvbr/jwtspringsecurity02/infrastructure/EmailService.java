package com.mvbr.jwtspringsecurity02.infrastructure;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(final String to, final String confirmationLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Confirmação de Cadastro");
            message.setText("Clique no link para confirmar seu cadastro: " + confirmationLink);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de confirmação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
