package course.java.sdm.engine.model.notifications;

import course.java.sdm.engine.model.notifications.baseNotification.StoreNotification;

public class OrderCompletedNotification extends StoreNotification {
    public OrderCompletedNotification (String zoneName, String storeName, String customerName) {
        super(NotificationType.ORDER_COMPLETED, zoneName, storeName, customerName);
    }
}
