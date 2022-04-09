package kata;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Delivery {

    private final Long id;
    private final String contactEmail;
    private final Coordinates coordinates;
    private LocalDateTime timeOfDelivery;
    private boolean arrived;
    private boolean onTime;

    public Delivery(
            Long id,
            String contactEmail,
            Coordinates coordinates,
            LocalDateTime timeOfDelivery,
            boolean arrived,
            boolean onTime
    ) {
        this.id = id;
        this.contactEmail = contactEmail;
        this.coordinates = coordinates;
        this.timeOfDelivery = timeOfDelivery;
        this.arrived = arrived;
        this.onTime = onTime;
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

    public LocalDateTime getTimeOfDelivery() {
        return timeOfDelivery;
    }

    public void setTimeOfDelivery(LocalDateTime timeOfDelivery) {
        this.timeOfDelivery = timeOfDelivery;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public boolean isOnTime() {
        return onTime;
    }

    public void setOnTime(boolean onTime) {
        this.onTime = onTime;
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
}
