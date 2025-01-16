package sk.tuke.service.impl;


// Importing required classes
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sk.tuke.service.EmailService;
import sk.tuke.service.dto.EmailDetails;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    public EmailServiceImpl (JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    // Method 1
    // To send a simple email
    public boolean sendSimpleMail(EmailDetails details)
    {
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            log.debug("Mail Sent Successfully...");
            return true;
        }

        catch (Exception e) {
            log.error("Sending email failed.");
            log.debug("message: " + e.getMessage());
            return false;
        }
    }

}
