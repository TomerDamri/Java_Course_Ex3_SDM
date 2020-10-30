package model.request;

import java.util.List;
import java.util.UUID;

import model.ItemToAddDTO;

public class AddStoreToZoneRequest {

    private UUID storeOwnerId;
    private String zoneName;
    private String storeName;
    private final Integer xCoordinate;
    private final Integer yCoordinate;
    private Integer deliveryPpk;
    private List<ItemToAddDTO> storeItems;

    public AddStoreToZoneRequest (UUID storeOwnerId,
                                  String zoneName,
                                  String storeName,
                                  Integer xCoordinate,
                                  Integer yCoordinate,
                                  Integer deliveryPpk,
                                  List<ItemToAddDTO> storeItems) {
        this.storeOwnerId = storeOwnerId;
        this.zoneName = zoneName;
        this.storeName = storeName;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.deliveryPpk = deliveryPpk;
        this.storeItems = storeItems;
    }

    public Integer getxCoordinate () {
        return xCoordinate;
    }

    public Integer getyCoordinate () {
        return yCoordinate;
    }

    public String getZoneName () {
        return zoneName;
    }

    public Integer getDeliveryPpk () {
        return deliveryPpk;
    }

    public List<ItemToAddDTO> getStoreItems () {
        return storeItems;
    }

    public String getStoreName () {
        return storeName;
    }

    public UUID getStoreOwnerId () {
        return storeOwnerId;
    }
}
