package edu.univ.lms.observer;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import edu.univ.lms.model.User;

/**
 * Sends real email notifications using Gmail SMTP.
 * <p>
 * This notifier relies on Gmail's SMTP service, which requires:
 * <ul>
 *     <li>A valid Gmail account</li>
 *     <li>A Gmail App Password (not the regular login password)</li>
 * </ul>
 * <p>
 * When attached to the {@code ReminderService}, this notifier will send
 * actual overdue reminders to the user's email address.
 */
public class RealEmailNotifier implements Observer {

    /** Gmail sender email address. */
    private String senderEmail;

    /** Gmail app password used for SMTP authentication. */
    private String senderPassword;

    /**
     * Creates a new real email notifier.
     *
     * @param senderEmail      Gmail address used to send the messages
     * @param senderPassword   Gmail App Password (NOT the standard Gmail password)
     */
    public RealEmailNotifier(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    /**
     * Sends an overdue reminder to the specified user.
     *
     * @param user    recipient user (email will be read from {@link User#getEmail()})
     * @param message email content body
     */
    @Override
    public void notify(User user, String message) {
        try {
            // SMTP configuration for Gmail
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            // Authenticate using Gmail App Password
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            // Construct the message
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
