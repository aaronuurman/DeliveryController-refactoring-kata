package kata;

import java.time.Duration;

import static kata.NotificationService.DATE_TIME_FORMATTER;

public final class EmailSender implements Sender {

    public static final String UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_EMAIL = "Your delivery to [%s,%s] is next, estimated time of arrival is in %s minutes. Be ready!";
    private static final String RECOMMENDATION_MESSAGE_TEMPLATE_FOR_EMAIL = "Regarding your delivery today at %s. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";
    private final SendgridEmailGateway emailGateway;

    public EmailSender(SendgridEmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    public void sendUpcomingDelivery(Delivery delivery, Duration eta) {
        var subject = "Your delivery will arrive soon";
        emailGateway.send(
                delivery.getContactEmail(),
                subject,
                UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_EMAIL.formatted(
                        delivery.getCoordinates().latitude(),
                        delivery.getCoordinates().longitude(),
                        eta.toMinutes()
                ));
    }

    public void sendRecommendToFriend(Delivery delivery) {
        var subject = "Your feedback is important to us";
        emailGateway.send(
                delivery.getContactEmail(),
                subject,
                RECOMMENDATION_MESSAGE_TEMPLATE_FOR_EMAIL.formatted(DATE_TIME_FORMATTER.format(delivery.getTimeOfDelivery()))
        );
    }

}