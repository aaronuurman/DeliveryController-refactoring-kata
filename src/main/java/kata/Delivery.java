package kata;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Delivery {

    private final Long id;
    private final String contactEmail;
    private final float latitude;
    private final float longitude;
    private LocalDateTime timeOfDelivery;
    private boolean arrived;
    private boolean onTime;

    public Delivery(
            Long id,
            String contactEmail,
            float latitude,
            float longitude,
            LocalDateTime timeOfDelivery,
            boolean arrived,
            boolean onTime
    ) {
        this.id = id;
        this.contactEmail = contactEmail;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
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
        return Float.compare(delivery.latitude, latitude) == 0 && Float.compare(delivery.longitude, longitude) == 0
                && arrived == delivery.arrived && onTime == delivery.onTime && id.equals(delivery.id)
                && contactEmail.equals(delivery.contactEmail) && timeOfDelivery.equals(delivery.timeOfDelivery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contactEmail, latitude, longitude, timeOfDelivery, arrived, onTime);
    }

    @Override
    public String toString() {
        return "Delivery{"
                + "id=" + id +
                ", contactEmail='" + contactEmail + '\''
                + ", latitude=" + latitude
                + ", longitude=" + longitude
                + ", timeOfDelivery=" + timeOfDelivery
                + ", arrived=" + arrived
                + ", onTime=" + onTime
                + '}';
    }
}
