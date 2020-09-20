package model;

public class SystemItemDTO {
    private final Integer id;
    private final String name;
    private final String purchaseCategory;
    private final Integer storesCount;
    private final Double avgPrice;
    private final Double ordersCount;

    public SystemItemDTO (Integer id, String name, String purchaseCategory, int storesCount, double avgPrice, double ordersCount) {
        this.id = id;
        this.name = name;
        this.purchaseCategory = purchaseCategory;
        this.storesCount = storesCount;
        this.avgPrice = avgPrice;
        this.ordersCount = ordersCount;
    }

    public int getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public String getPurchaseCategory () {
        return purchaseCategory;
    }

    public int getStoresCount () {
        return storesCount;
    }

    public double getAvgPrice () {
        return avgPrice;
    }

    public double getOrdersCount () {
        return ordersCount;
    }
}
