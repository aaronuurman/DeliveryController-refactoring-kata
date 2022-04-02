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

    public static List<Delivery> createOnTimeScheduleWithTwoDeliveries(LocalDateTime deliveryTime) {
        return List.of(
                createDeliveryWithTime(deliveryTime),
                new Delivery(
                        124L,
                        "test2@example.com",
                        58.366291f,
                        28.739834f,
                        deliveryTime.plusMinutes(8),
                        false,
                        false
                )
        );
    }

    public static List<Delivery> createLateScheduleWithThreeDeliveries(LocalDateTime deliveryTime) {
        return List.of(
                createDeliveryWithTime(deliveryTime.minusMinutes(5)),
                new Delivery(
                        124L,
                        "test2@example.com",
                        58.366191f,
                        27.739824f,
                        deliveryTime.minusMinutes(8),
                        false,
                        false
                ),
                new Delivery(
                        125L,
                        "test3@example.com",
                        58.366291f,
                        28.739834f,
                        deliveryTime,
                        false,
                        false
                )
        );
    }

}
