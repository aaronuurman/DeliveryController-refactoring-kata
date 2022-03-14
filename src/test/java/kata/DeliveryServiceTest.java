package kata;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static kata.DeliveryAssert.assertThatDelivery;
import static kata.DeliveryService.ALLOWED_DELAY_IN_MINUTES;
import static kata.DeliveryService.DATE_TIME_FORMATTER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        //@ExtendWith(SilentTestCommitRevertMainExtension.class)
class DeliveryServiceTest {

    static List<Integer> deliveryTimes() {
        return List.of(-2, 0, ALLOWED_DELAY_IN_MINUTES - 1);
    }

    @ParameterizedTest
    @MethodSource("deliveryTimes")
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
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class));

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

    @Test
    void onDelivery_late_correctData() {
        // Arrange
        var deliveryTime = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var deliveryEvent = new DeliveryEvent(123L, deliveryTime, 58.366190f, 26.739820f);
        var scheduledDelivery = new Delivery(
                123L,
                "test1@example.com",
                58.366191f,
                26.739824f,
                deliveryTime.minusMinutes(ALLOWED_DELAY_IN_MINUTES + 1),
                false,
                false
        );
        var deliverySchedule = List.of(scheduledDelivery);
        var deliveryService = new DeliveryService(mock(SendgridEmailGateway.class));

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
    void onDelivery_recommendation_email_sent() {
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
        var deliveryService = new DeliveryService(sendgridEmailGateway);

        // Act
        deliveryService.on(deliveryEvent, deliverySchedule);

        // Arrange
        verify(sendgridEmailGateway).send(
                scheduledDelivery.getContactEmail(),
                "Your feedback is important to us",
                "Regarding your delivery today at 2022-03-14 16:34. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>"
        );
    }
}
