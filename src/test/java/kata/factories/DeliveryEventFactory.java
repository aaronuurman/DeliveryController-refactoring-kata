package kata.factories;

import java.time.LocalDateTime;

import kata.DeliveryEvent;

import static kata.NotificationService.DATE_TIME_FORMATTER;
import static kata.factories.CoordinatesFactory.TARTU_TURU_29B;

public final class DeliveryEventFactory {

    private DeliveryEventFactory() {
    }

    public static final LocalDateTime DELIVERY_TIME = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);

    public static DeliveryEvent createDeliveryEvent() {
        return new DeliveryEvent(123L, DELIVERY_TIME, TARTU_TURU_29B.latitude(), TARTU_TURU_29B.longitude());
    }

    public static DeliveryEvent createDeliveryEvent(long id) {
        return new DeliveryEvent(id, DELIVERY_TIME, TARTU_TURU_29B.latitude(), TARTU_TURU_29B.longitude());
    }
}
