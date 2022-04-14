package kata;

import java.util.List;

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

    public Delivery getNextDelivery() {
        if (deliveryList.size() > index + 1) {
            return deliveryList.get(index + 1);
        }
        return null;
    }

    public boolean weHaveMultipleDeliveriesAndCurrentDeliveryIsNotTheFirstOne() {
        return deliveryList.size() > 1 && index > 0;
    }

    public Delivery getPreviousDelivery() {
        return deliveryList.get(index - 1);
    }
}
