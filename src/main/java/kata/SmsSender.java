package kata;

import java.time.Duration;

public final class SmsSender {

    public static final String UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_SMS = "Your delivery arrives in %s minutes.";
    private final SmsGateway smsGateway;

    public SmsSender(SmsGateway smsGateway) {
        this.smsGateway = smsGateway;
    }

    public void sendUpcomingDeliverySms(Delivery delivery, Duration eta) {
        smsGateway.send(delivery.getPhoneNumber(), UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_SMS.formatted(eta.toMinutes()));
    }

}