package model;

public class ZoneDTO {
    private String zoneOwnerName;
    private String zoneName;
    private Integer numOfItemsForSale;
    private Integer numOfStoresInZone;
    private Integer numOfExecutedOrders;
    private Double avgOrdersPrice;

    public ZoneDTO (String zoneOwnerName,
                    String zoneName,
                    Integer numOfItemsForSale,
                    Integer numOfStoresInZone,
                    Integer numOfExecutedOrders,
                    Double avgOrdersPrice) {
        this.zoneOwnerName = zoneOwnerName;
        this.zoneName = zoneName;
        this.numOfItemsForSale = numOfItemsForSale;
        this.numOfStoresInZone = numOfStoresInZone;
        this.numOfExecutedOrders = numOfExecutedOrders;
        this.avgOrdersPrice = avgOrdersPrice;
    }

    public String getZoneOwnerName () {
        return zoneOwnerName;
    }

    public String getZoneName () {
        return zoneName;
    }

    public Integer getNumOfItemsForSale () {
        return numOfItemsForSale;
    }

    public Integer getNumOfStoresInZone () {
        return numOfStoresInZone;
    }

    public Integer getNumOfExecutedOrders () {
        return numOfExecutedOrders;
    }

    public Double getAvgOrdersPrice () {
        return avgOrdersPrice;
    }
}
