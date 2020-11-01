package course.java.sdm.engine.model.notifications;

import course.java.sdm.engine.model.notifications.baseNotification.StoreNotification;

public class FeedbackAddedNotification extends StoreNotification {

    public FeedbackAddedNotification (NotificationType notificationType, String zoneName, String storeName, String customerName) {
        super(NotificationType.NEW_FEEDBACK_ADDED, zoneName, storeName, customerName);
    }
}
