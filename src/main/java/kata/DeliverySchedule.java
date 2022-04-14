package kata;

import java.util.List;
import java.util.Optional;

public class DeliverySchedule {

    private final List<Delivery> deliveryList;
    private int index = -1; // TODO: Add test case.

    public DeliverySchedule(List<Delivery> deliveryList) {
        this.deliveryList = deliveryList;
    }

    public Delivery find(long id) {
        for (int i = 0; i < deliveryList.size(); i++) {
            Delivery delivery = deliveryList.get(i);
            if (id == delivery.getId()) {
                index = i;
                return delivery;
            }
        }
        throw new IllegalStateException();
    }

    public boolean weHaveMultipleDeliveriesAndCurrentDeliveryIsNotTheFirstOne() {
        return deliveryList.size() > 1 && index > 0;
    }

    public Delivery getPreviousDelivery() {
        return deliveryList.get(index - 1);
    }

    public Optional<Delivery> findNextDelivery() {
        Delivery result = null;
        if (deliveryList.size() > index + 1) {
            result = deliveryList.get(index + 1);
        }
        return Optional.ofNullable(result);
    }
}
