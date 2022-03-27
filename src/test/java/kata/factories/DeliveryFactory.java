package kata.factories;

import java.time.LocalDateTime;
import java.util.List;

import kata.Delivery;

public final class DeliveryFactory {

    private DeliveryFactory() {
    }

    private static Delivery createDeliveryWithTime(LocalDateTime deliveryTime) {
        return new Delivery(
                123L,
                "test1@example.com",
                58.366191f,
                26.739824f,
                deliveryTime,
                false,
                false
        );
    }

    public static List<Delivery> createScheduleWithSingleDelivery(LocalDateTime deliveryTime) {
        return List.of(createDeliveryWithTime(deliveryTime));
    }

}
