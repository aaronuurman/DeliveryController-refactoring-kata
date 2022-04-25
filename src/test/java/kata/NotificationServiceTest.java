package kata;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static kata.NotificationService.DATE_TIME_FORMATTER;
import static kata.factories.CoordinatesFactory.VORU_LEPA_2;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NotificationServiceTest {

    private SendgridEmailGateway sendgridEmailGatewayMock;
    private SmsGateway smsMock;

    @BeforeEach
    void setUp() {
        sendgridEmailGatewayMock = mock(SendgridEmailGateway.class);
        smsMock = mock(SmsGateway.class);
    }

    @Test
    void recommendToFriend_withPhoneNumber_sendsSms() {
        // Arrange
        var time = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var notificationService = new NotificationService(sendgridEmailGatewayMock, smsMock);
        var message = "Regarding your delivery today at 2022-03-14 16:34. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>";
        String phoneNumber = "+372 555555555";

        // Act
        notificationService.recommendToFriend(new Delivery(
                124L,
                "test2@example.com",
                VORU_LEPA_2,
                time,
                false,
                false,
                phoneNumber
        ));

        // Assert
        verify(smsMock, times(1)).send(phoneNumber, message);
    }

    @Test
    void upcomingDelivery_withPhoneNumber_sendsSms() {
        // Arrange
        var time = LocalDateTime.parse("2022-03-14 16:34", DATE_TIME_FORMATTER);
        var notificationService = new NotificationService(sendgridEmailGatewayMock, smsMock);
        String phoneNumber = "+372 555555555";
        var message = "Your delivery to [57.840694,27.004316] is next, estimated time of arrival is in 0 minutes. Be ready!";

        // Act
        notificationService.upcomingDelivery(new Delivery(
                124L,
                "test2@example.com",
                VORU_LEPA_2,
                time,
                false,
                false,
                phoneNumber
        ), Duration.ZERO);

        // Assert
        verify(smsMock, times(1)).send(phoneNumber, message);
    }

}
