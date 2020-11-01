package course.java.sdm.engine.model.notifications.base;

public abstract class StoreNotification extends Notification {

    private String customerName;

    public StoreNotification (NotificationType notificationType, String zoneName, String storeName, String customerName) {
        super(notificationType, zoneName, storeName);
        this.customerName = customerName;
    }

    public String getCustomerName () {
        return customerName;
    }
}
