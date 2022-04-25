package kata;

import java.time.Duration;
import java.util.List;

import jakarta.inject.Singleton;

@Singleton
// TODO: use class instead of record
public record DeliveryService(NotificationService notificationService, MapService mapService) {

    public static final int ALLOWED_DELAY_IN_MINUTES = 10;

    public List<Delivery> on(DeliveryEvent deliveryEvent, List<Delivery> deliverySchedule) {
        // TODO: Rename to something meaningful.
        var deliverySchedule2 = new DeliverySchedule(deliverySchedule);
        Delivery currentDelivery = deliverySchedule2.find(deliveryEvent.id());
        currentDelivery.update(deliveryEvent);

        notificationService.recommendToFriend(currentDelivery);

        deliverySchedule2.getPreviousDelivery().ifPresent(delivery -> maybeUpdateAverageSpeed(currentDelivery, delivery));
        deliverySchedule2.findNextDelivery().ifPresent(delivery -> informNextDeliveryRecipientAboutNewEta(deliveryEvent, delivery));
        return deliverySchedule;
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
