package kata.factories;

import java.time.LocalDateTime;

import kata.DeliveryEvent;

import static kata.DeliveryService.DATE_TIME_FORMATTER;

public final class DeliveryEventFactory {

    private DeliveryEventFactory() {
    }

    public static final LocalDateTime DELIVERY_TIME = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);

    public static DeliveryEvent createDeliveryEvent() {
        return new DeliveryEvent(123L, DELIVERY_TIME, 58.366190f, 26.739820f);
    }

    public static DeliveryEvent createDeliveryEvent(long id) {
        return new DeliveryEvent(id, DELIVERY_TIME, 58.366190f, 26.739820f);
    }
}
