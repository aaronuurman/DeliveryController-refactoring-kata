package kata;

import java.time.LocalDateTime;

public record DeliveryEvent(long id, LocalDateTime timeOfDelivery, float latitude, float longitude) {

    public Coordinates getCoordinates() {
        return new Coordinates(latitude(), longitude());
    }
}
