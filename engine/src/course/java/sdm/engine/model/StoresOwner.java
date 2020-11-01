package course.java.sdm.engine.model;

import java.util.*;

import course.java.sdm.engine.model.notifications.base.Notification;
import course.java.sdm.engine.model.notifications.subscriber.NotificationsSubscriber;

public class StoresOwner implements NotificationsSubscriber {
    private final UUID id;
    private String name;
    private Map<String, Map<Integer, SystemStore>> zoneToOwnedStores;
    private List<Notification> oldNotifications;
    private List<Notification> newNotifications;
    private final Object newNotificationsLock = new Object();

    public StoresOwner (UUID id, String name) {
        this.id = id;
        this.name = name;
        this.zoneToOwnedStores = new HashMap<>();
        newNotifications = new ArrayList<>();
        oldNotifications = new ArrayList<>();
    }

    public UUID getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public Map<String, Map<Integer, SystemStore>> getZoneToOwnedStores () {
        return zoneToOwnedStores;
    }

    public List<Notification> getNewNotifications () {
        List<Notification> latestNotifications;
        synchronized (newNotificationsLock) {
            latestNotifications = new ArrayList<>(newNotifications);
            newNotifications.clear();
        }
        oldNotifications.addAll(latestNotifications);
        return latestNotifications;
    }

    @Override
    public void notificationAdded (Notification notification) {
        synchronized (newNotificationsLock) {
            newNotifications.add(notification);
        }
    }
}
