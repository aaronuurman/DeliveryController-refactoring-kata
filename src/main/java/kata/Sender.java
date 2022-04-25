package kata;

import java.time.Duration;

public interface Sender {

    void sendUpcomingDelivery(Delivery delivery, Duration eta);

    void sendRecommendToFriend(Delivery delivery);

}
