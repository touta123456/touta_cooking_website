package com.toutacooking.springboot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class WelcomeMailSenderService {

	private final Logger log = LoggerFactory.getLogger(WelcomeMailSenderService.class);
	
    private final JavaMailSender mailSender;

    public WelcomeMailSenderService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

    public void sendWelcomeEmail(String email) {
        try {
	    	SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(email);
	        message.setSubject("Welcome to CookingApp!");
	        message.setText("Your account has been created.");
	        mailSender.send(message);
	        }
		catch (Exception e) {
			// why not create a dedicated dead letter queue later
			log.error("Error sending the welcome email to {} due to : {}", email, e.getMessage());
		}
    }
    
}