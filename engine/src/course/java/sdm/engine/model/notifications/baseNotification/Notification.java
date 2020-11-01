package course.java.sdm.engine.model.notifications.baseNotification;

public abstract class Notification {

    private NotificationType notificationType;
    private String zoneName;
    private String storeName;

    public Notification (NotificationType notificationType, String zoneName, String storeName) {
        this.notificationType = notificationType;
        this.zoneName = zoneName;
        this.storeName = storeName;
    }

    public NotificationType getEventType () {
        return notificationType;
    }

    public String getZoneName () {
        return zoneName;
    }

    public String getStoreName () {
        return storeName;
    }

    public enum NotificationType {
        ORDER_COMPLETED, NEW_FEEDBACK_ADDED, NEW_STORE_ADDED
    }
}
