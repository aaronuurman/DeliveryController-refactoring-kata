package kata;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Delivery {

  private Long id;
  private String contactEmail;
  private float latitude;
  private float longitude;
  private LocalDateTime timeOfDelivery;
  private boolean arrived;
  private boolean onTime;

  public Delivery(Long id, String contactEmail, float latitude, float longitude,
      LocalDateTime timeOfDelivery, boolean arrived, boolean onTime) {
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

  public void setId(long id) {
    this.id = id;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public float getLatitude() {
    return latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }

  public LocalDateTime getTimeOfDelivery() {
    return timeOfDelivery;
  }

  public void setTimeOfDelivery(LocalDateTime timeOfDelivery) {
    this.timeOfDelivery = timeOfDelivery;
  }

  public boolean isArrived() {
    return arrived;
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
    return Float.compare(delivery.latitude, latitude) == 0
        && Float.compare(delivery.longitude, longitude) == 0 && arrived == delivery.arrived
        && onTime == delivery.onTime && id.equals(delivery.id) && contactEmail.equals(
        delivery.contactEmail) && timeOfDelivery.equals(delivery.timeOfDelivery);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, contactEmail, latitude, longitude, timeOfDelivery, arrived, onTime);
  }
}
