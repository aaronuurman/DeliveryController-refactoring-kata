package kata;

import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Singleton
public class DeliveryService {

  private final SendgridEmailGateway emailGateway;
  private final MapService mapService = new MapService();

  public DeliveryService() {
    emailGateway = new SendgridEmailGateway();
  }

  public List<Delivery> on(DeliveryEvent deliveryEvent, List<Delivery> deliverySchedule) {
    Delivery nextDelivery = null;
    for (int i = 0; i < deliverySchedule.size(); i++) {
      Delivery delivery = deliverySchedule.get(i);
      if (deliveryEvent.id() == delivery.getId()) {
        delivery.setArrived(true);
        Duration d = Duration.between(delivery.getTimeOfDelivery(), deliveryEvent.timeOfDelivery());

        if (d.toMinutes() < 10) {
          delivery.setOnTime(true);
        }
        delivery.setTimeOfDelivery(deliveryEvent.timeOfDelivery());
        String message = "Regarding your delivery today at %s. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>".formatted(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(delivery.getTimeOfDelivery()));
        emailGateway.send(delivery.getContactEmail(), "Your feedback is important to us", message
        );
        if (deliverySchedule.size() > i + 1) {
          nextDelivery = deliverySchedule.get(i + 1);
        }

        if (!delivery.isOnTime() && deliverySchedule.size() > 1 && i > 0) {
          var previousDelivery = deliverySchedule.get(i - 1);
          Duration elapsedTime = Duration.between(previousDelivery.getTimeOfDelivery(),
              delivery.getTimeOfDelivery());
          mapService.updateAverageSpeed(
              elapsedTime, previousDelivery.getLatitude(),
              previousDelivery.getLongitude(), delivery.getLatitude(),
              delivery.getLongitude());
        }
      }
    }

    if (nextDelivery != null) {
      var nextEta = mapService.calculateETA(
          deliveryEvent.latitude(), deliveryEvent.longitude(),
          nextDelivery.getLatitude(), nextDelivery.getLongitude());
      var message =

          "Your delivery to [%s,%s] is next, estimated time of arrival is in %s minutes. Be ready!".formatted(
              nextDelivery.getLatitude(), nextDelivery.getLongitude(), nextEta.getSeconds() / 60);
      emailGateway.send(nextDelivery.getContactEmail(), "Your delivery will arrive soon", message
      );
    }
    return deliverySchedule;
  }
}
