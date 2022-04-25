package kata;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class NotificationService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SendgridEmailGateway emailGateway;
    private final SmsGateway smsGateway;
    private EmailSender emailSender;
    private SmsSender smsSender;

    public NotificationService(SendgridEmailGateway sendgridEmailGateway) {
        this(sendgridEmailGateway, null);
    }

    public NotificationService(SendgridEmailGateway sendgridEmailGateway, SmsGateway smsGateway) {
        emailGateway = sendgridEmailGateway;
        this.smsGateway = smsGateway;
        emailSender = new EmailSender(emailGateway);
        smsSender = new SmsSender(smsGateway);
    }

    public void upcomingDelivery(Delivery delivery, Duration eta) {
        if (delivery.hasPhoneNumber()) {
            smsSender.sendUpcomingDelivery(delivery, eta);
        } else {
            emailSender.sendUpcomingDelivery(delivery, eta);
        }
    }

    public void recommendToFriend(Delivery delivery) {
        if (delivery.hasPhoneNumber()) {
            smsSender.sendRecommendToFriend(delivery);
        } else {
            emailSender.sendRecommendToFriend(delivery);
        }
    }

}
