package kata;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static kata.factories.DeliveryEventFactory.DELIVERY_TIME;
import static kata.factories.DeliveryFactory.createDeliveryWithoutPhoneNumber;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EmailSenderTest {

    private SendgridEmailGateway sendgridEmailGatewayMock;

    @BeforeEach
    void setUp() {
        sendgridEmailGatewayMock = mock(SendgridEmailGateway.class);
    }

    @Test
    void sendUpcomingDelivery_recommendationEmailSent() {
        // Arrange
        var emailSender = new EmailSender(sendgridEmailGatewayMock);
        var delivery = createDeliveryWithoutPhoneNumber(DELIVERY_TIME);

        // Act
        emailSender.sendRecommendToFriend(delivery);

        // Assert
        verify(sendgridEmailGatewayMock).send(
                "test1@example.com",
                "Your feedback is important to us",
                "Regarding your delivery today at 2022-03-14 16:34. How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>"
        );
    }

    @Test
    void sendUpcomingDelivery_sendUpcomingDelivery() {
        // Arrange
        var emailSender = new EmailSender(sendgridEmailGatewayMock);
        var delivery = createDeliveryWithoutPhoneNumber(DELIVERY_TIME);

        // Act
        emailSender.sendUpcomingDelivery(delivery, Duration.ofMinutes(10));

        // Assert
        verify(sendgridEmailGatewayMock).send(
                "test1@example.com",
                "Your delivery will arrive soon",
                "Your delivery to [58.36619,26.73982] is next, estimated time of arrival is in 10 minutes. Be ready!"
        );
    }

}
