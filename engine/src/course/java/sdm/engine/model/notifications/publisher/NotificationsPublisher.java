package course.java.sdm.engine.model.notifications.publisher;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import course.java.sdm.engine.model.notifications.base.Notification;
import course.java.sdm.engine.model.notifications.subscriber.NotificationsSubscriber;

public abstract class NotificationsPublisher {

    private final Set<NotificationsSubscriber> subscribers = new CopyOnWriteArraySet<>();

    public void addNotificationsSubscriber (NotificationsSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void broadcast (Notification notification) {
        subscribers.forEach(subscriber -> notifySafely(notification, subscriber));
    }

    private void notifySafely (Notification notification, NotificationsSubscriber subscriber) {
        try {
            subscriber.notificationAdded(notification);
        }
        catch (Exception unexpected) {
            // appropriate exception handling goes here...
        }
    }
}
