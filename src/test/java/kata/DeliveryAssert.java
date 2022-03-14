package kata;

import java.time.LocalDateTime;

import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DeliveryAssert extends AbstractAssert<DeliveryAssert, Delivery> {

    protected DeliveryAssert(Delivery actual) {
        super(actual, DeliveryAssert.class);
    }

    public static DeliveryAssert assertThatDelivery(Delivery delivery) {
        return new DeliveryAssert(delivery);
    }
    
    public DeliveryAssert hasId(Long expected) {
        assertThat(actual.getId()).isEqualTo(expected);
        return this;
    }

    public DeliveryAssert hasContactEmail(String expected) {
        assertThat(actual.getContactEmail()).isEqualTo(expected);
        return this;
    }

    public DeliveryAssert hasLatitude(Float expected) {
        assertThat(actual.getLatitude()).isEqualTo(expected);
        return this;
    }

    public DeliveryAssert hasLongitude(Float expected) {
        assertThat(actual.getLongitude()).isEqualTo(expected);
        return this;
    }

    public DeliveryAssert hasTimeOfDelivery(LocalDateTime expected) {
        assertThat(actual.getTimeOfDelivery()).isEqualTo(expected);
        return this;
    }

    public DeliveryAssert hasArrived(boolean expected) {
        assertThat(actual.isArrived()).isEqualTo(expected);
        return this;
    }

    public DeliveryAssert isOnTime(boolean expected) {
        assertThat(actual.isOnTime()).isEqualTo(expected);
        return this;
    }
}
