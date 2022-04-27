package kata;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class NotificationService {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Sender emailSender;
    private Sender smsSender;

    public NotificationService(EmailSender emailSender1, SmsSender smsSender1) {
        emailSender = emailSender1;
        smsSender = smsSender1;
    }

    private Sender getSender(Delivery delivery) {
        Sender sender;
        if (delivery.hasPhoneNumber()) {
            sender = smsSender;
        } else {
            sender = emailSender;
        }
        return sender;
    }

    public void upcomingDelivery(Delivery delivery, Duration eta) {
        Sender sender = getSender(delivery);
        sender.sendUpcomingDelivery(delivery, eta);
    }

    public void recommendToFriend(Delivery delivery) {
        Sender sender = getSender(delivery);
        sender.sendRecommendToFriend(delivery);
    }

}
