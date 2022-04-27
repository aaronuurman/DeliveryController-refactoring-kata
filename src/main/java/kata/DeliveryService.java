package kata;

import java.time.Duration;
import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public class DeliveryService {

    private final NotificationService notificationService;
    private final MapService mapService;

    public DeliveryService(NotificationService notificationService, MapService mapService) {
        this.notificationService = notificationService;
        this.mapService = mapService;
    }

    public static final int ALLOWED_DELAY_IN_MINUTES = 10;

    public List<Delivery> on(DeliveryEvent deliveryEvent, List<Delivery> deliveryList) {
        var deliverySchedule = new DeliverySchedule(deliveryList);
        Delivery currentDelivery = deliverySchedule.find(deliveryEvent.id());
        currentDelivery.update(deliveryEvent);

        notificationService.recommendToFriend(currentDelivery);

        deliverySchedule.getPreviousDelivery().ifPresent(delivery -> maybeUpdateAverageSpeed(currentDelivery, delivery));
        deliverySchedule.findNextDelivery().ifPresent(delivery -> informNextDeliveryRecipientAboutNewEta(deliveryEvent, delivery));
        return deliveryList;
    }

    private void maybeUpdateAverageSpeed(Delivery currentDelivery, Delivery delivery) {
        if (!currentDelivery.isOnTime()) {
            Duration elapsedTime = Duration.between(delivery.getTimeOfDelivery(), currentDelivery.getTimeOfDelivery());
            mapService.updateAverageSpeed(
                    elapsedTime,
                    delivery.getCoordinates(),
                    currentDelivery.getCoordinates()
            );
        }
    }

    private void informNextDeliveryRecipientAboutNewEta(DeliveryEvent deliveryEvent, Delivery delivery) {
        var from = deliveryEvent.getCoordinates();
        var to = delivery.getCoordinates();
        var nextEta = mapService.calculateETAInMinutes(from, to);
        notificationService.upcomingDelivery(delivery, nextEta);
    }

}
