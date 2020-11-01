package course.java.sdm.engine.model.notifications;

import course.java.sdm.engine.model.Location;
import course.java.sdm.engine.model.notifications.baseNotification.Notification;

public class StoreAddedNotification extends Notification {

    private String newStoreOwnerNAme;
    private Location newStoreLocation;
    private Integer numOfStoreItems;
    private Integer numOfZoneItems;

    public StoreAddedNotification (String zoneName,
                                   String storeName,
                                   String newStoreOwnerNAme,
                                   Location newStoreLocation,
                                   Integer numOfStoreItems,
                                   Integer numOfZoneItems) {
        super(NotificationType.NEW_STORE_ADDED, zoneName, storeName);
        this.newStoreOwnerNAme = newStoreOwnerNAme;
        this.newStoreLocation = newStoreLocation;
        this.numOfStoreItems = numOfStoreItems;
        this.numOfZoneItems = numOfZoneItems;
    }
}
