package course.java.sdm.engine.model.notifications;

import course.java.sdm.engine.model.Location;
import course.java.sdm.engine.model.notifications.base.Notification;

public class StoreAddedNotification extends Notification {

    private String newStoreOwnerName;
    private Location newStoreLocation;
    private Integer numOfStoreItems;
    private Integer numOfZoneItems;

    public StoreAddedNotification (String zoneName,
                                   String storeName,
                                   String newStoreOwnerName,
                                   Location newStoreLocation,
                                   Integer numOfStoreItems,
                                   Integer numOfZoneItems) {
        super(NotificationType.NEW_STORE_ADDED, zoneName, storeName);
        this.newStoreOwnerName = newStoreOwnerName;
        this.newStoreLocation = newStoreLocation;
        this.numOfStoreItems = numOfStoreItems;
        this.numOfZoneItems = numOfZoneItems;
    }

    @Override
    public String toString () {
        return String.format("The new store '%s' was added by '%s' in '%s' location in '%s'.\nThe store sales '%s/%s' items from zone items.",
                             getStoreName(),
                             newStoreOwnerName,
                             newStoreLocation,
                             getZoneName(),
                             numOfStoreItems,
                             numOfZoneItems);
    }
}
