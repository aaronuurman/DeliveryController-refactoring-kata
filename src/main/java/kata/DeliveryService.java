package kata;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public class DeliveryService {

    public static final int ALLOWED_DELAY_IN_MINUTES = 10;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String RECOMMENDATION_MESSAGE_TEMPLATE = "Regarding your delivery today at %s. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";
    private static final String UPCOMING_DELIVERY_MESSAGE_TEMPLATE = "Your delivery to [%s,%s] is next, estimated time of arrival is in %s minutes. Be ready!";

    private final SendgridEmailGateway emailGateway;
    private final MapService mapService;

    public DeliveryService(SendgridEmailGateway sendgridEmailGateway, MapService mapService) {
        this.emailGateway = sendgridEmailGateway;
        this.mapService = mapService;
    }

    public List<Delivery> on(DeliveryEvent deliveryEvent, List<Delivery> deliverySchedule) {
        Delivery nextDelivery = null;
        for (int i = 0; i < deliverySchedule.size(); i++) {
            Delivery delivery = deliverySchedule.get(i);
            if (deliveryEvent.id() == delivery.getId()) {
                delivery.setArrived(true);
                Duration d = Duration.between(delivery.getTimeOfDelivery(), deliveryEvent.timeOfDelivery());

                if (d.toMinutes() < ALLOWED_DELAY_IN_MINUTES) {
                    delivery.setOnTime(true);
                }
                delivery.setTimeOfDelivery(deliveryEvent.timeOfDelivery());
                String message = RECOMMENDATION_MESSAGE_TEMPLATE.formatted(DATE_TIME_FORMATTER.format(delivery.getTimeOfDelivery()));
                emailGateway.send(delivery.getContactEmail(), "Your feedback is important to us", message);
                if (deliverySchedule.size() > i + 1) {
                    nextDelivery = deliverySchedule.get(i + 1);
                }

                if (!delivery.isOnTime() && deliverySchedule.size() > 1 && i > 0) {
                    var previousDelivery = deliverySchedule.get(i - 1);
                    Duration elapsedTime = Duration.between(previousDelivery.getTimeOfDelivery(), delivery.getTimeOfDelivery());
                    mapService.updateAverageSpeed(
                            elapsedTime,
                            previousDelivery.getCoordinates(),
                            delivery.getCoordinates()
                    );
                }
            }
        }

        if (nextDelivery != null) {
            var from = new Coordinates(deliveryEvent.latitude(), deliveryEvent.longitude());
            var to = nextDelivery.getCoordinates();
            var nextEta = mapService.calculateETAInMinutes(from, to);
            var message = UPCOMING_DELIVERY_MESSAGE_TEMPLATE.formatted(
                    nextDelivery.getCoordinates().latitude(),
                    nextDelivery.getCoordinates().longitude(),
                    nextEta.getSeconds() / 60
            );
            emailGateway.send(nextDelivery.getContactEmail(), "Your delivery will arrive soon", message);
        }
        return deliverySchedule;
    }
}
