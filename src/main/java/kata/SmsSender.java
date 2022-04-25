package kata;

import java.time.Duration;

public class SmsSender implements Sender {

    public static final String UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_SMS = "Your delivery arrives in %s minutes.";
    private static final String RECOMMENDATION_MESSAGE_TEMPLATE_FOR_SMS = "How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";
    private final SmsGateway smsGateway;

    public SmsSender(SmsGateway smsGateway) {
        this.smsGateway = smsGateway;
    }

    public void sendUpcomingDelivery(Delivery delivery, Duration eta) {
        smsGateway.send(delivery.getPhoneNumber(), UPCOMING_DELIVERY_MESSAGE_TEMPLATE_FOR_SMS.formatted(eta.toMinutes()));
    }

    public void sendRecommendToFriend(Delivery delivery) {
        smsGateway.send(delivery.getPhoneNumber(), RECOMMENDATION_MESSAGE_TEMPLATE_FOR_SMS);
    }

}