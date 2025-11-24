package edu.univ.lms;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Real email notifier using Gmail SMTP.
 * Requires:
 *  - Gmail address
 *  - App Password (NOT regular password)
 */
public class RealEmailNotifier implements Observer {

    private String senderEmail;
    private String senderPassword;

    public RealEmailNotifier(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    @Override
    public void notify(User user, String message) {
        try {
            // SMTP configuration
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            // Auth session using Gmail App Password
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            // Build the email
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(senderEmail));
            emailMessage.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(user.getEmail())
            );
            emailMessage.setSubject("Library Overdue Reminder");
            emailMessage.setText(message);

            // Send email
            Transport.send(emailMessage);

            System.out.println("Email sent successfully to: " + user.getEmail());

        } catch (MessagingException e) {
            System.out.println("Error sending email to: " + user.getEmail());
            e.printStackTrace();
        }
    }
}
