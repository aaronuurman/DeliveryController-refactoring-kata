package kata;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class NotificationService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SendgridEmailGateway emailGateway;
    private final SmsGateway smsGateway;
    private EmailSender emailSender;
    private SmsSender smsSender;

    private static final String RECOMMENDATION_MESSAGE_TEMPLATE_FOR_EMAIL = "Regarding your delivery today at %s. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";
    private static final String RECOMMENDATION_MESSAGE_TEMPLATE_FOR_SMS = "How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";

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
            smsSender.sendUpcomingDeliverySms(delivery, eta);
        } else {
            emailSender.sendUpcomingDeliveryEmail(delivery, eta);
        }
    }

    public void recommendToFriend(Delivery delivery) {
        if (delivery.hasPhoneNumber()) {
            sendRecommendToFriendSms(delivery);
        } else {
            sendRecommendToFriendEmail(delivery);
        }
    }

    private void sendRecommendToFriendEmail(Delivery delivery) {
        var subject = "Your feedback is important to us";
        emailGateway.send(
                delivery.getContactEmail(),
                subject,
                RECOMMENDATION_MESSAGE_TEMPLATE_FOR_EMAIL.formatted(DATE_TIME_FORMATTER.format(delivery.getTimeOfDelivery()))
        );
    }

    private void sendRecommendToFriendSms(Delivery delivery) {
        smsGateway.send(delivery.getPhoneNumber(), RECOMMENDATION_MESSAGE_TEMPLATE_FOR_SMS);
    }

}
