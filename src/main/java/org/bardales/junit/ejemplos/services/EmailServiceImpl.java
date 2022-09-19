package org.bardales.junit.ejemplos.services;

public class EmailServiceImpl implements EmailService {

    public String sendMail(String email) {
        return String.format("Email successfully sent to %1$s", email);
    }

}
