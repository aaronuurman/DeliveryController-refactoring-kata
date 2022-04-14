package kata;

import java.time.Duration;
import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public record DeliveryService(NotificationService notificationService, MapService mapService) {

    public static final int ALLOWED_DELAY_IN_MINUTES = 10;

    public List<Delivery> on(DeliveryEvent deliveryEvent, List<Delivery> deliverySchedule) {
        var deliverySchedule2 = new DeliverySchedule(deliverySchedule);
        Delivery currentDelivery = deliverySchedule2.find(deliveryEvent.id());
        currentDelivery.update(deliveryEvent);

        notificationService.recommendToFriend(currentDelivery);

        if (!currentDelivery.isOnTime() && deliverySchedule2.weHaveMultipleDeliveriesAndCurrentDeliveryIsNotTheFirstOne()) {
            var previousDelivery = deliverySchedule2.getPreviousDelivery();
            Duration elapsedTime = Duration.between(previousDelivery.getTimeOfDelivery(), currentDelivery.getTimeOfDelivery());
            mapService.updateAverageSpeed(
                    elapsedTime,
                    previousDelivery.getCoordinates(),
                    currentDelivery.getCoordinates()
            );
        }

        Delivery nextDelivery = deliverySchedule2.getNextDelivery();
        if (nextDelivery != null) {
            var from = new Coordinates(deliveryEvent.latitude(), deliveryEvent.longitude());
            var to = nextDelivery.getCoordinates();
            var nextEta = mapService.calculateETAInMinutes(from, to);
            notificationService.upcomingDelivery(nextDelivery, nextEta);
        }
        return deliverySchedule;
    }

}
