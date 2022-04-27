package kata;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static kata.factories.DeliveryEventFactory.DELIVERY_TIME;
import static kata.factories.DeliveryFactory.createDeliveryWithPhoneNumber;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SmsSenderTest {

    private SmsGateway smsGatewayMock;

    @BeforeEach
    void setUp() {
        smsGatewayMock = mock(SmsGateway.class);
    }

    @Test
    void sendRecommendToFriend_sendsSms() {
        // Arrange
        var smsSender = new SmsSender(smsGatewayMock);
        var delivery = createDeliveryWithPhoneNumber(DELIVERY_TIME);

        // Act
        smsSender.sendRecommendToFriend(delivery);

        // Assert
        verify(smsGatewayMock, times(1)).send(
                delivery.getPhoneNumber(),
                "How likely would you be to recommend this delivery service to a friend? Click <a href='url'>here</a>"
        );
    }

    @Test
    void sendUpcomingDelivery_sendsSms() {
        // Arrange
        var smsSender = new SmsSender(smsGatewayMock);
        var delivery = createDeliveryWithPhoneNumber(DELIVERY_TIME);

        // Act
        smsSender.sendUpcomingDelivery(delivery, Duration.ofMinutes(5));

        // Assert
        verify(smsGatewayMock, times(1)).send(
                delivery.getPhoneNumber(),
                "Your delivery arrives in 5 minutes."
        );
    }
}
