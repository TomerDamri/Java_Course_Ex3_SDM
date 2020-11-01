package course.java.sdm.engine.model.notifications;

import course.java.sdm.engine.model.notifications.base.StoreNotification;

public class OrderCompletedNotification extends StoreNotification {
    public OrderCompletedNotification (String zoneName, String storeName, String customerName) {
        super(NotificationType.ORDER_COMPLETED, zoneName, storeName, customerName);
    }

    @Override
    public String toString () {
        return String.format("A new order was created by '%s' to '%s' in '%s'.", getCustomerName(), getStoreName(), getZoneName());
    }
}
