package kata;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import io.micronaut.core.util.StringUtils;

public class NotificationService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SendgridEmailGateway emailGateway;
    private final SmsGateway smsGateway;

    private static final String UPCOMING_DELIVERY_MESSAGE_TEMPLATE = "Your delivery to [%s,%s] is next, estimated time of arrival is in %s minutes. Be ready!";
    private static final String RECOMMENDATION_MESSAGE_TEMPLATE = "Regarding your delivery today at %s. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";

    public NotificationService(SendgridEmailGateway sendgridEmailGateway) {
        this(sendgridEmailGateway, null);
    }

    public NotificationService(SendgridEmailGateway sendgridEmailGateway, SmsGateway smsGateway) {
        emailGateway = sendgridEmailGateway;
        this.smsGateway = smsGateway;
    }

    public void upcomingDelivery(Delivery delivery, Duration eta) {
        var subject = "Your delivery will arrive soon";
        var message = UPCOMING_DELIVERY_MESSAGE_TEMPLATE.formatted(
                delivery.getCoordinates().latitude(),
                delivery.getCoordinates().longitude(),
                eta.toMinutes()
        );
        emailGateway.send(delivery.getContactEmail(), subject, message);
    }

    public void recommendToFriend(Delivery delivery) {
        var subject = "Your feedback is important to us";
        var message = RECOMMENDATION_MESSAGE_TEMPLATE.formatted(DATE_TIME_FORMATTER.format(delivery.getTimeOfDelivery()));
        if (StringUtils.isEmpty(delivery.getPhoneNumber())) {
            emailGateway.send(delivery.getContactEmail(), subject, message);
        } else {
            smsGateway.send(delivery.getPhoneNumber(), message);
        }
    }

}
