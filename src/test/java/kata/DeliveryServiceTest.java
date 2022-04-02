package kata;

import java.time.Duration;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import static kata.DeliveryService.ALLOWED_DELAY_IN_MINUTES;
import static kata.factories.DeliveryEventFactory.DELIVERY_TIME;
import static kata.factories.DeliveryEventFactory.createDeliveryEvent;
import static kata.factories.DeliveryFactory.createLateScheduleWithThreeDeliveries;
import static kata.factories.DeliveryFactory.createOnTimeScheduleWithTwoDeliveries;
import static kata.factories.DeliveryFactory.createScheduleWithSingleDelivery;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeliveryServiceTest {

    @Test
    void onDelivery_withInAllowedTime_allDeliveryPropertiesAreFilled() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createScheduleWithSingleDelivery(DELIVERY_TIME);
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

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
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

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
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

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
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

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
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

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
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

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
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

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
        var sendgridEmailGateway = mock(SendgridEmailGateway.class);
        var deliveryService = new DeliveryService(sendgridEmailGateway, mock(MapService.class));

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Arrange
        verify(sendgridEmailGateway).send(
                schedule.get(0).getContactEmail(),
                "Your feedback is important to us",
                "Regarding your delivery today at 2022-03-14 16:34. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>"
        );
    }

    @Test
    void onDelivery_whenNextScheduled_upcomingDeliveryEmailSent() {
        // Arrange
        var deliveryEvent = createDeliveryEvent();
        var schedule = createOnTimeScheduleWithTwoDeliveries(DELIVERY_TIME.plusMinutes(2));
        var sendgridEmailGateway = mock(SendgridEmailGateway.class);
        var eta = Duration.ofMinutes(10);
        var mapService = mock(MapService.class);
        when(mapService.calculateETA(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(eta);
        var deliveryService = new DeliveryService(sendgridEmailGateway, mapService);

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Arrange
        verify(sendgridEmailGateway).send(
                schedule.get(1).getContactEmail(),
                "Your delivery will arrive soon",
                "Your delivery to [58.36629,28.739834] is next, estimated time of arrival is in 10 minutes. Be ready!"
        );
    }

    @Test
    void onDelivery_multipleDeliveriesLate_newAverageSpeed() {
        // Arrange
        var deliveryEvent = createDeliveryEvent(124);
        var schedule = createLateScheduleWithThreeDeliveries(DELIVERY_TIME.minusMinutes(15));
        var sendgridEmailGateway = mock(SendgridEmailGateway.class);
        var eta = Duration.ofMinutes(10);
        var mapService = mock(MapService.class);
        when(mapService.calculateETA(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(eta);
        var deliveryService = new DeliveryService(sendgridEmailGateway, mapService);

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Arrange
        verify(sendgridEmailGateway).send(
                schedule.get(2).getContactEmail(),
                "Your delivery will arrive soon",
                "Your delivery to [58.36629,28.739834] is next, estimated time of arrival is in 10 minutes. Be ready!"
        );
    }

    @Test
    void onDelivery_averageSpeedIsUpdated() {
        // Arrange
        var deliveryEvent = createDeliveryEvent(124);
        var schedule = createLateScheduleWithThreeDeliveries(DELIVERY_TIME.minusMinutes(15));
        var sendgridEmailGateway = mock(SendgridEmailGateway.class);
        var eta = Duration.ofMinutes(10);
        var mapService = mock(MapService.class);
        when(mapService.calculateETA(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(eta);
        var deliveryService = new DeliveryService(sendgridEmailGateway, mapService);

        // Act
        deliveryService.on(deliveryEvent, schedule);

        // Assert
        verify(mapService, times(1)).updateAverageSpeed(
                Duration.ofMinutes(20),
                58.36619f,
                26.739824f,
                58.36619f,
                27.739824f
        );
    }
}
