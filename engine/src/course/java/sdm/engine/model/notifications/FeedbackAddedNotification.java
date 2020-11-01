package course.java.sdm.engine.model.notifications;

import course.java.sdm.engine.model.notifications.base.StoreNotification;

public class FeedbackAddedNotification extends StoreNotification {

    public FeedbackAddedNotification (String zoneName, String storeName, String customerName) {
        super(NotificationType.NEW_FEEDBACK_ADDED, zoneName, storeName, customerName);
    }

    @Override
    public String toString () {
        return String.format("A new feedback was added by '%s' to '%s' in '%s'.", getCustomerName(), getStoreName(), getZoneName());
    }
}
