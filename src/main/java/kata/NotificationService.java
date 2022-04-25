package kata;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class NotificationService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final String UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_SMS = "Your delivery arrives in %s minutes.";

    private final SendgridEmailGateway emailGateway;
    private final SmsGateway smsGateway;

    private static final String UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_EMAIL = "Your delivery to [%s,%s] is next, estimated time of arrival is in %s minutes. Be ready!";
    private static final String RECOMMENDATION_MESSAGE_TEMPLATE_FOR_EMAIL = "Regarding your delivery today at %s. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";
    private static final String RECOMMENDATION_MESSAGE_TEMPLATE_FOR_SMS = "How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";

    public NotificationService(SendgridEmailGateway sendgridEmailGateway) {
        this(sendgridEmailGateway, null);
    }

    public NotificationService(SendgridEmailGateway sendgridEmailGateway, SmsGateway smsGateway) {
        emailGateway = sendgridEmailGateway;
        this.smsGateway = smsGateway;
    }

    public void upcomingDelivery(Delivery delivery, Duration eta) {
        var subject = "Your delivery will arrive soon";
        var message = UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_EMAIL.formatted(
                delivery.getCoordinates().latitude(),
                delivery.getCoordinates().longitude(),
                eta.toMinutes()
        );
        if (delivery.hasPhoneNumber()) {
            smsGateway.send(delivery.getPhoneNumber(), UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_SMS.formatted(eta.toMinutes()));
        } else {
            emailGateway.send(delivery.getContactEmail(), subject, message);
        }
    }

    public void recommendToFriend(Delivery delivery) {
        var subject = "Your feedback is important to us";
        if (delivery.hasPhoneNumber()) {
            var message = RECOMMENDATION_MESSAGE_TEMPLATE_FOR_SMS;
            smsGateway.send(delivery.getPhoneNumber(), message);
        } else {
            var message = RECOMMENDATION_MESSAGE_TEMPLATE_FOR_EMAIL.formatted(DATE_TIME_FORMATTER.format(delivery.getTimeOfDelivery()));
            emailGateway.send(delivery.getContactEmail(), subject, message);
        }
    }

}
