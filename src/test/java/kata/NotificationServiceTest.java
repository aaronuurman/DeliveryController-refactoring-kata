package kata;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static kata.factories.DeliveryEventFactory.DELIVERY_TIME;
import static kata.factories.DeliveryFactory.createDeliveryWithPhoneNumber;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NotificationServiceTest {

    private EmailSender emailSenderMock;
    private SmsSender smsSenderMock;

    @BeforeEach
    void setUp() {
        emailSenderMock = mock(EmailSender.class);
        smsSenderMock = mock(SmsSender.class);
    }

    @Test
    void recommendToFriend_withPhoneNumber_sendsSms() {
        // Arrange
        var notificationService = new NotificationService(emailSenderMock, smsSenderMock);
        var delivery = createDeliveryWithPhoneNumber(DELIVERY_TIME);

        // Act
        notificationService.recommendToFriend(delivery);

        // Assert
        verify(smsSenderMock, times(1)).sendRecommendToFriend(delivery);
    }

    @Test
    void upcomingDelivery_withPhoneNumber_sendsSms() {
        // Arrange
        var notificationService = new NotificationService(emailSenderMock, smsSenderMock);
        var delivery = createDeliveryWithPhoneNumber(DELIVERY_TIME);
        Duration duration = Duration.ZERO;

        // Act
        notificationService.upcomingDelivery(delivery, duration);

        // Assert
        verify(smsSenderMock, times(1)).sendUpcomingDelivery(delivery, duration);
    }

}
