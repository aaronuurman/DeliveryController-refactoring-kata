package kata.factories;

import java.time.LocalDateTime;
import java.util.List;

import kata.Delivery;

import static kata.factories.CoordinatesFactory.TARTU_TURU_29B;
import static kata.factories.CoordinatesFactory.VILJANDI_VAKSALI_7;
import static kata.factories.CoordinatesFactory.VORU_LEPA_2;

public final class DeliveryFactory {

    private DeliveryFactory() {
    }

    private static Delivery createDeliveryWithTime(LocalDateTime deliveryTime) {
        return new Delivery(
                123L,
                "test1@example.com",
                TARTU_TURU_29B.getLatitude(),
                TARTU_TURU_29B.getLongitude(),
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
                        VORU_LEPA_2.getLatitude(),
                        VORU_LEPA_2.getLongitude(),
                        deliveryTime.plusMinutes(8),
                        false,
                        false
                )
        );
    }

    /**
     * This case is valid for example when something unexpected happened on the road.
     */
    public static List<Delivery> createLateScheduleWithThreeDeliveries(LocalDateTime deliveryTime) {
        return List.of(
                createDeliveryWithTime(deliveryTime.minusMinutes(132)),
                new Delivery(
                        124L,
                        "test2@example.com",
                        VORU_LEPA_2.getLatitude(),
                        VORU_LEPA_2.getLongitude(),
                        deliveryTime.minusMinutes(65),
                        false,
                        false
                ),
                new Delivery(
                        125L,
                        "test3@example.com",
                        VILJANDI_VAKSALI_7.getLatitude(),
                        VILJANDI_VAKSALI_7.getLongitude(),
                        deliveryTime,
                        false,
                        false
                )
        );
    }

}
