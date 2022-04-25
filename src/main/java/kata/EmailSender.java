package kata;

import java.time.Duration;

public final class EmailSender {

    public static final String UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_EMAIL = "Your delivery to [%s,%s] is next, estimated time of arrival is in %s minutes. Be ready!";
    private final SendgridEmailGateway emailGateway;

    public EmailSender(SendgridEmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    public void sendUpcomingDeliveryEmail(Delivery delivery, Duration eta) {
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

}