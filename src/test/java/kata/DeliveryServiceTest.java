package kata;

import java.time.Duration;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static kata.DeliveryService.ALLOWED_DELAY_IN_MINUTES;
import static kata.factories.CoordinatesFactory.VORU_LEPA_2;
import static kata.factories.DeliveryEventFactory.DELIVERY_TIME;
import static kata.factories.DeliveryEventFactory.createDeliveryEvent;
import static kata.factories.DeliveryFactory.createLateScheduleWithThreeDeliveries;
import static kata.factories.DeliveryFactory.createOnTimeScheduleWithTwoDeliveries;
import static kata.factories.DeliveryFactory.createScheduleWithSingleDelivery;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeliveryServiceTest {

    private MapService mapServiceMock;
    private SendgridEmailGateway sendgridEmailGatewayMock;
    private DeliveryService deliveryService;

    @BeforeEach
    void setup() {
        mapServiceMock = mock(MapService.class);
        sendgridEmailGatewayMock = mock(SendgridEmailGateway.class);
        deliveryService = new DeliveryService(sendgridEmailGatewayMock, mapServiceMock);
    }

    @Test
    void onDelivery_withInAllowedTime_allDeliveryPropertiesAreFilled() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME);

        // Act
        var result = deliveryService.on(deliveryEvent, schedule);

        // Assert
        Approvals.verify(result);
    }

    @Test
    void onDelivery_late_allDeliveryPropertiesAreFilled() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME.minusMinutes(ALLOWED_DELAY_IN_MINUTES));

        // Act
        var result = deliveryService.on(deliveryEvent, schedule);

        // Assert
        Approvals.verify(result);
    }

    @Test
    void onDelivery_twoMinutesLate_onTimeTrue() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME.plusMinutes(2));

        // Act
        var result = deliveryService.on(deliveryEvent, schedule);

        // Assert
        assertTrue(result.get(0).isOnTime());
    }

    @Test
    void onDelivery_exactlyOnTime_onTimeTrue() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME);

        // Act
        var result = deliveryService.on(deliveryEvent, schedule);

        // Assert
        assertTrue(result.get(0).isOnTime());
    }

    @Test
    void onDelivery_secondBeforeAllowedDelay_onTimeTrue() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME.minusSeconds(1));

        // Act
        var result = deliveryService.on(deliveryEvent, schedule);

        // Assert
        assertTrue(result.get(0).isOnTime());
    }

    @Test
    void onDelivery_exactlyOnAllowedDelay_onTimeFalse() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME.minusMinutes(ALLOWED_DELAY_IN_MINUTES));

        // Act
        var result = deliveryService.on(deliveryEvent, schedule);

        // Assert
        assertFalse(result.get(0).isOnTime());
    }

    @Test
    void onDelivery_secondOverAllowedDelay_onTimeFalse() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME.minusMinutes(ALLOWED_DELAY_IN_MINUTES).minusSeconds(1));

        // Act
        var result = deliveryService.on(deliveryEvent, schedule);

        // Assert
        assertFalse(result.get(0).isOnTime());
    }

    @Test
    void onDelivery_recommendationEmailSent() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME.plusMinutes(2));

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Arrange
        verify(sendgridEmailGatewayMock).send(
                "test1@example.com",
                "Your feedback is important to us",
                "Regarding your delivery today at 2022-03-14 16:34. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>"
        );
    }

    @Test
    void onDelivery_whenNextScheduled_upcomingDeliveryEmailSent() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createOnTimeScheduleWithTwoDeliveries(DELIVERY_TIME.plusMinutes(2));
        when(mapServiceMock.calculateETAInMinutes(any(), any())).thenReturn(Duration.ofMinutes(10));

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Arrange
        verify(sendgridEmailGatewayMock).send(
                "test2@example.com",
                "Your delivery will arrive soon",
                "Your delivery to [57.840694,27.004316] is next, estimated time of arrival is in 10 minutes. Be ready!"
        );
    }

    @Test
    void onDelivery_multipleDeliveriesLate_newAverageSpeed() {
        // Arrange
        var deliveryEvent = createDeliveryEvent(124);
        var schedule = createLateScheduleWithThreeDeliveries(DELIVERY_TIME.minusMinutes(15));
        when(mapServiceMock.calculateETAInMinutes(any(), any())).thenReturn(Duration.ofMinutes(10));

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Arrange
        verify(sendgridEmailGatewayMock).send(
                "test3@example.com",
                "Your delivery will arrive soon",
                "Your delivery to [58.362286,25.58746] is next, estimated time of arrival is in 10 minutes. Be ready!"
        );
    }

    @Test
    void onDelivery_averageSpeedIsUpdated() {
        // Arrange
        var deliveryEvent = createDeliveryEvent(124);
        var schedule = createLateScheduleWithThreeDeliveries(DELIVERY_TIME.minusMinutes(15));
        when(mapServiceMock.calculateETAInMinutes(any(), any())).thenReturn(Duration.ofMinutes(10));

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Assert
        verify(mapServiceMock, times(1)).updateAverageSpeed(Duration.ofMinutes(147),
                                                            new Coordinates(deliveryEvent.latitude(), deliveryEvent.longitude()),
                                                            VORU_LEPA_2);
    }

    // TODO: Create test case, where delivery is early with next scheduled delivery. Validate that also then. mapService.updateAverageSpeed() is called.
    // TODO: Potential bug in time calculation of the upcoming delivery email.

}
