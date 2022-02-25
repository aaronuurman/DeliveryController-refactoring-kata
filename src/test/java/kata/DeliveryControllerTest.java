package kata;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DeliveryControllerTest {

    private EmailGateway emailGateway = mock(EmailGateway.class);

    @Test
    void updatesDeliveryWhenArrivedOnTime() {
        Delivery delivery = deliveryThatTookMinutesAndSecondsToArrive(9, 59);

        Assertions.assertAll(
                () -> assertThat(delivery.arrived()).isTrue(),
                () -> assertThat(delivery.onTime()).isTrue(),
                () -> assertThat(delivery.timeOfDelivery()).isEqualTo(LocalDateTime.of(2022, 2, 25, 15, 17, 59)));
    }

    @Test
    void updatesDeliveryWhenArrivedAndIsLate() {
        Delivery delivery = deliveryThatTookMinutesToArrive(10);

        assertThat(delivery.onTime()).isFalse();
    }

    @Test
    void sendEmail() {
        deliveryThatTookMinutesToArrive(11);

        verify(emailGateway).send(
                "lars@example.com",
                "Your feedback is important to us",
                "Regarding your delivery today at 2022-02-25 15:19. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>");
    }

    private Delivery deliveryThatTookMinutesToArrive(int minutes) {
        return deliveryThatTookMinutesAndSecondsToArrive(minutes, 0);
    }

    private Delivery deliveryThatTookMinutesAndSecondsToArrive(int minutes, int seconds) {
        LocalDateTime timeOfOrder = LocalDateTime.of(2022, 2, 25, 15, 8);
        Delivery d1 = new Delivery(123L, "lars@example.com", new Location(123f, 123f), timeOfOrder, false, false);

        List<Delivery> deliverySchedule = List.of(d1);
        DeliveryController deliveryController = new DeliveryController(deliverySchedule, emailGateway);

        LocalDateTime timeOfDelivery = timeOfOrder.plusMinutes(minutes).plusSeconds(seconds);
        deliveryController.updateDelivery(new DeliveryEvent(123L, timeOfDelivery, new Location(123f, 123f)));
        return d1;
    }

    // marksTimeOfDeliveryWhenDelivered
    // marksDeliveryAsOnTimeWhenDeliveredWithin10Minutes() {
    // marksDeliveryAsNotOnTimeWhenDeliveredMoreThan10MinutesAfterOrdering() {
    // sendCorrectEmailMessage -> verify invocation of emailGateway

    // TODO
    // deliverySchedule with 1 element
    // deliverySchedule with 2 elements
    // when late and have more, verify that we call MapService to update aveverage speed
    // sendCorrectEmailMessage for next delivery -> verify invocation of emailGateway
}
