package com.example.actionprice.sendEmail;

import com.example.actionprice.exception.InvalidEmailAddressException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.SendFailedException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail") // 이거 덕에 @Value 안 써도 됨
@Setter
public class SMTPConfiguration {

  private String host;
  private String port;
  private String username;
  private String password;

  public void sendEmail(String receiverEmail, String subject, String content) {

    Session session = getSession();

    try {
      Message message = composeMessage(session, receiverEmail, subject, content);

      Transport.send(message);
    }
    catch(SendFailedException e){
     e.printStackTrace();
     throw new InvalidEmailAddressException(e.getMessage());
    } catch (MessagingException e) {
      e.printStackTrace();
      throw new InvalidEmailAddressException(e.getMessage());
    }

  }

  private Session getSession() {
    Properties properties = new Properties();
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", port);
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");

    Session session = Session.getDefaultInstance(properties, new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    return session;
  }

  private Message composeMessage(Session session, String receiverEmail, String subject, String content) throws MessagingException {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(username));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
    message.setSubject(subject);
    message.setText(content);

    return message;
  }
}
