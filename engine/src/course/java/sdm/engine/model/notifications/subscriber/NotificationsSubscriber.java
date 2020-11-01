package course.java.sdm.engine.model.notifications.subscriber;

import course.java.sdm.engine.model.notifications.base.Notification;

public interface NotificationsSubscriber {

    void notificationAdded (Notification notification);
}
