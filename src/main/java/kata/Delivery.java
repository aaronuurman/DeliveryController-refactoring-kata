package kata;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import io.micronaut.core.util.StringUtils;

public final class Delivery {

    private final Long id;
    private final String contactEmail;
    private final Coordinates coordinates;
    private LocalDateTime timeOfDelivery;
    private boolean arrived;
    private boolean onTime;
    private final String phoneNumber;

    public Delivery(
            Long id,
            String contactEmail,
            Coordinates coordinates,
            LocalDateTime timeOfDelivery,
            boolean arrived,
            boolean onTime
    ) {
        this(id, contactEmail, coordinates, timeOfDelivery, arrived, onTime, null);
    }

    public Delivery(
            long id,
            String contactEmail,
            Coordinates coordinates,
            LocalDateTime timeOfDelivery,
            boolean arrived,
            boolean onTime,
            String phoneNumber
    ) {
        this.id = id;
        this.contactEmail = contactEmail;
        this.coordinates = coordinates;
        this.timeOfDelivery = timeOfDelivery;
        this.arrived = arrived;
        this.onTime = onTime;
        this.phoneNumber = phoneNumber;
    }

    public long getId() {
        return id;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getTimeOfDelivery() {
        return timeOfDelivery;
    }

    public boolean isOnTime() {
        return onTime;
    }

    public void update(DeliveryEvent deliveryEvent) {
        Duration duration = Duration.between(getTimeOfDelivery(), deliveryEvent.timeOfDelivery());
        if (duration.toMinutes() < DeliveryService.ALLOWED_DELAY_IN_MINUTES) {
            this.onTime = true;
        }
        this.arrived = true;
        this.timeOfDelivery = deliveryEvent.timeOfDelivery();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Delivery delivery = (Delivery) o;
        return arrived == delivery.arrived
                && onTime == delivery.onTime
                && Objects.equals(id, delivery.id)
                && Objects.equals(contactEmail, delivery.contactEmail)
                && Objects.equals(coordinates, delivery.coordinates)
                && Objects.equals(timeOfDelivery, delivery.timeOfDelivery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contactEmail, coordinates, timeOfDelivery, arrived, onTime);
    }

    @Override
    public String toString() {
        return "Delivery{"
                + "id=" + id +
                ", contactEmail='" + contactEmail + '\''
                + ", latitude=" + coordinates.latitude()
                + ", longitude=" + coordinates.longitude()
                + ", timeOfDelivery=" + timeOfDelivery
                + ", arrived=" + arrived
                + ", onTime=" + onTime
                + '}';
    }

    public boolean hasPhoneNumber() {
        return StringUtils.isNotEmpty(getPhoneNumber());
    }
}
