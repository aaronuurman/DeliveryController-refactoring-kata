package kata;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static kata.DeliveryAssert.assertThatDelivery;
import static kata.DeliveryService.ALLOWED_DELAY_IN_MINUTES;
import static kata.DeliveryService.DATE_TIME_FORMATTER;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeliveryServiceTest {

    static List<Integer> onTimeDeliveryTimes() {
        return List.of(-2, 0, ALLOWED_DELAY_IN_MINUTES - 1);
    }

    static List<Integer> lateDeliveryTimes() {
        return List.of(ALLOWED_DELAY_IN_MINUTES, ALLOWED_DELAY_IN_MINUTES + 1);
    }

    @ParameterizedTest
    @MethodSource("onTimeDeliveryTimes")
    void onDelivery_withInAllowedTime_correctData(Integer time) {
        // Arrange
        var deliveryTime = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var deliveryEvent = new DeliveryEvent(123L, deliveryTime, 58.366190f, 26.739820f);
        var scheduledDelivery = new Delivery(
                123L,
                "test1@example.com",
                58.366191f,
                26.739824f,
                deliveryTime.minusMinutes(time),
                false,
                false
        );
        var deliverySchedule = List.of(scheduledDelivery);
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

        // Act
        var result = deliveryService.on(deliveryEvent, deliverySchedule);

        // Assert
        assertThatDelivery(result.get(0))
                .hasId(deliveryEvent.id())
                .hasContactEmail(scheduledDelivery.getContactEmail())
                .hasLatitude(scheduledDelivery.getLatitude())
                .hasLongitude(scheduledDelivery.getLongitude())
                .hasTimeOfDelivery(deliveryEvent.timeOfDelivery())
                .hasArrived(true)
                .isOnTime(true);
    }

    @ParameterizedTest
    @MethodSource("lateDeliveryTimes")
    void onDelivery_late_correctData(Integer time) {
        // Arrange
        var deliveryTime = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var deliveryEvent = new DeliveryEvent(123L, deliveryTime, 58.366190f, 26.739820f);
        var scheduledDelivery = new Delivery(
                123L,
                "test1@example.com",
                58.366191f,
                26.739824f,
                deliveryTime.minusMinutes(time),
                false,
                false
        );
        var deliverySchedule = List.of(scheduledDelivery);
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class), mock(MapService.class));

        // Act
        var result = deliveryService.on(deliveryEvent, deliverySchedule);

        // Assert
        assertThatDelivery(result.get(0))
                .hasId(deliveryEvent.id())
                .hasContactEmail(scheduledDelivery.getContactEmail())
                .hasLatitude(scheduledDelivery.getLatitude())
                .hasLongitude(scheduledDelivery.getLongitude())
                .hasTimeOfDelivery(deliveryEvent.timeOfDelivery())
                .hasArrived(true)
                .isOnTime(false);
    }

    @Test
    void onDelivery_recommendationEmailSent() {
        // Arrange
        var deliveryTime = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var deliveryEvent = new DeliveryEvent(123L, deliveryTime, 58.366190f, 26.739820f);
        var scheduledDelivery = new Delivery(
                123L,
                "test1@example.com",
                58.366191f,
                26.739824f,
                deliveryTime.plusMinutes(2),
                false,
                false
        );
        var deliverySchedule = List.of(scheduledDelivery);
        var sendgridEmailGateway = mock(SendgridEmailGateway.class);
        var deliveryService = new DeliveryService(sendgridEmailGateway, mock(MapService.class));

        // Act
        deliveryService.on(deliveryEvent, deliverySchedule);

        // Arrange
        verify(sendgridEmailGateway).send(
                scheduledDelivery.getContactEmail(),
                "Your feedback is important to us",
                "Regarding your delivery today at 2022-03-14 16:34. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>"
        );
    }

    @Test
    void onDelivery_whenNextScheduled_upcomingDeliveryEmailSent() {
        // Arrange
        var deliveryTime = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var deliveryEvent = new DeliveryEvent(123L, deliveryTime, 58.366190f, 26.739820f);
        var scheduledDelivery = new Delivery(
                123L,
                "test1@example.com",
                58.366191f,
                26.739824f,
                deliveryTime.plusMinutes(2),
                false,
                false
        );
        var nextScheduledDelivery = new Delivery(
                124L,
                "test2@example.com",
                58.366291f,
                28.739834f,
                deliveryTime.plusMinutes(10),
                false,
                false
        );
        var deliverySchedule = List.of(scheduledDelivery, nextScheduledDelivery);
        var sendgridEmailGateway = mock(SendgridEmailGateway.class);
        var eta = Duration.ofMinutes(10);
        var mapService = mock(MapService.class);
        when(mapService.calculateETA(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(eta);
        var deliveryService = new DeliveryService(sendgridEmailGateway, mapService);

        // Act
        deliveryService.on(deliveryEvent, deliverySchedule);

        // Arrange
        verify(sendgridEmailGateway).send(
                nextScheduledDelivery.getContactEmail(),
                "Your delivery will arrive soon",
                "Your delivery to [58.36629,28.739834] is next, estimated time of arrival is in 10 minutes. Be ready!"
        );
    }

    @Test
    void onDelivery_multipleDeliveriesLate_newAverageSpeed() {
        // Arrange
        var deliveryTime = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var deliveryEvent = new DeliveryEvent(124L, deliveryTime, 58.366190f, 26.739820f);
        var nextScheduledDelivery = new Delivery(
                125L,
                "test3@example.com",
                58.366291f,
                28.739834f,
                deliveryTime.minusMinutes(15),
                false,
                false
        );
        var deliverySchedule = List.of(
                new Delivery(
                        123L,
                        "test1@example.com",
                        58.366191f,
                        26.739824f,
                        deliveryTime.minusMinutes(20),
                        false,
                        false),
                new Delivery(
                        124L,
                        "test2@example.com",
                        58.366191f,
                        27.739824f,
                        deliveryTime.minusMinutes(23),
                        false,
                        false),
                nextScheduledDelivery
        );
        var sendgridEmailGateway = mock(SendgridEmailGateway.class);
        var eta = Duration.ofMinutes(10);
        var mapService = mock(MapService.class);
        when(mapService.calculateETA(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(eta);
        var deliveryService = new DeliveryService(sendgridEmailGateway, mapService);

        // Act
        deliveryService.on(deliveryEvent, deliverySchedule);

        // Arrange
        verify(sendgridEmailGateway).send(
                nextScheduledDelivery.getContactEmail(),
                "Your delivery will arrive soon",
                "Your delivery to [58.36629,28.739834] is next, estimated time of arrival is in 10 minutes. Be ready!"
        );
    }

    @Test
    void onDelivery_averageSpeedIsUpdated() {
        // Arrange
        var deliveryTime = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var deliveryEvent = new DeliveryEvent(124L, deliveryTime, 58.366190f, 26.739820f);
        var nextScheduledDelivery = new Delivery(
                125L,
                "test3@example.com",
                58.366291f,
                28.739834f,
                deliveryTime.minusMinutes(15),
                false,
                false
        );
        var deliverySchedule = List.of(
                new Delivery(
                        123L,
                        "test1@example.com",
                        58.366191f,
                        26.739824f,
                        deliveryTime.minusMinutes(20),
                        false,
                        false),
                new Delivery(
                        124L,
                        "test2@example.com",
                        58.366191f,
                        27.739824f,
                        deliveryTime.minusMinutes(23),
                        false,
                        false),
                nextScheduledDelivery
        );
        var sendgridEmailGateway = mock(SendgridEmailGateway.class);
        var eta = Duration.ofMinutes(10);
        var mapService = mock(MapService.class);
        when(mapService.calculateETA(anyFloat(), anyFloat(), anyFloat(), anyFloat())).thenReturn(eta);
        var deliveryService = new DeliveryService(sendgridEmailGateway, mapService);

        // Act
        deliveryService.on(deliveryEvent, deliverySchedule);

        // Assert
        verify(mapService, times(1)).updateAverageSpeed(Duration.ofMinutes(20), 58.36619f, 26.739824f, 58.36619f, 27.739824f);
    }
}
