package sa.edu.kau.fcit.cpit252.project.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class confirmEmailMessage {
    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String toEmail, String description, double totalCost) {
        try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("nousi2020@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Booking Confirmation | Sport Hub");
            String htmlContent = """
                    <!DOCTYPE html>
                <html lang="en" dir="ltr">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: sans-serif; background: #f8fafc; padding: 20px; text-align: center; }
                        .receipt { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); max-width: 400px; margin: auto; }
                        h1 { color: #0f766e; }
                        .details { text-align: left; margin: 25px 0; border-top: 1px dashed #ccc; padding-top: 15px; line-height: 1.8; }
                        .total { font-size: 1.2rem; font-weight: bold; color: #b91c1c; }
                    </style>
                </head>
                <body>
                    <div class="receipt">
                        <h1>Booking Confirmed! 🎉</h1>
                        <div class="details">""" + 
                            
                            "<p><strong>Description:</strong> " + description + " </p>" +
                            """
                            <p class ="total"><strong>Total Cost: </strong> """
                                    
                                     + totalCost +  """ 
                                     SAR</p>
                            """
                                     +
                            """
                                    
                                    </div>
                    </div>
                </body>
                </html>
                    """;
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println(" HTML Email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
}
