package kata;

import java.time.Duration;
import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public record DeliveryService(NotificationService notificationService, MapService mapService) {

    public static final int ALLOWED_DELAY_IN_MINUTES = 10;

    public List<Delivery> on(DeliveryEvent deliveryEvent, List<Delivery> deliverySchedule) {
        Delivery nextDelivery = null;
        for (int i = 0; i < deliverySchedule.size(); i++) {
            Delivery delivery = deliverySchedule.get(i);
            if (deliveryEvent.id() == delivery.getId()) {
                delivery.update(deliveryEvent);

                notificationService.recommendToFriend(delivery);

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
            notificationService.upcomingDelivery(nextDelivery, nextEta);
        }
        return deliverySchedule;
    }

}
