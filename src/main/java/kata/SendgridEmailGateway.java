package kata;

import static org.slf4j.LoggerFactory.getLogger;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
import org.slf4j.Logger;

public class SendgridEmailGateway {

  private static final Logger log = getLogger(SendgridEmailGateway.class);

  public void send(String to, String subject, String message) {
    Email from = new Email("deliveries@example.com");
    Content content = new Content("text/plain", message);
    Mail mail = new Mail(from, subject, new Email(to), content);

    SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));

    try {
      Request request = new Request();
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sg.api(request);
      log.info("{}: {}", response.getStatusCode(), response.getBody());
      if (response.getStatusCode() != 200) {
        throw new RuntimeException(response.getBody());
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
