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
    private DeliveryService deliveryService;
    private NotificationService notificationServiceMock;

    @BeforeEach
    void setup() {
        mapServiceMock = mock(MapService.class);
        notificationServiceMock = mock(NotificationService.class);
        deliveryService = new DeliveryService(notificationServiceMock, mapServiceMock);
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
        verify(notificationServiceMock).recommendToFriend(schedule.get(0));
    }

    @Test
    void onDelivery_whenNextScheduled_upcomingDeliveryEmailSent() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createOnTimeScheduleWithTwoDeliveries(DELIVERY_TIME.plusMinutes(2));
        Duration duration = Duration.ofMinutes(10);
        when(mapServiceMock.calculateETAInMinutes(any(), any())).thenReturn(duration);

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Arrange
        verify(notificationServiceMock).upcomingDelivery(schedule.get(1), duration);
    }

    @Test
    void onDelivery_multipleDeliveriesLate_newAverageSpeed() {
        // Arrange
        var deliveryEvent = createDeliveryEvent(124);
        var schedule = createLateScheduleWithThreeDeliveries(DELIVERY_TIME.minusMinutes(15));
        Duration duration = Duration.ofMinutes(10);
        when(mapServiceMock.calculateETAInMinutes(any(), any())).thenReturn(duration);

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Arrange
        verify(notificationServiceMock).upcomingDelivery(schedule.get(2), duration);
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
        verify(mapServiceMock, times(1)).updateAverageSpeed(
                Duration.ofMinutes(147),
                new Coordinates(deliveryEvent.latitude(), deliveryEvent.longitude()),
                VORU_LEPA_2);
    }

    // TODO: Create test case, where delivery is early with next scheduled delivery. Validate that also then. mapService.updateAverageSpeed() is called.
    // TODO: Potential bug in time calculation of the upcoming delivery email.

}
