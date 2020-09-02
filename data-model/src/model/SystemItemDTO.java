package model;

public class SystemItemDTO {
    private final ItemDTO item;
    private final int storesCount;
    private final double avgPrice;
    private final int ordersCount;

    public SystemItemDTO (ItemDTO item, int storesCount, double avgPrice, int ordersCount) {
        this.item = item;
        this.storesCount = storesCount;
        this.avgPrice = avgPrice;
        this.ordersCount = ordersCount;
    }

    public ItemDTO getItem () {
        return item;
    }

    public int getId () {
        return item.getId();
    }

    public String getName () {
        return item.getName();
    }

    public String getPurchaseCategory () {
        return item.getPurchaseCategory();
    }

    public int getStoresCount () {
        return storesCount;
    }

    public double getAvgPrice () {
        return avgPrice;
    }

    public int getOrdersCount () {
        return ordersCount;
    }

    @Override
    public String toString () {
        StringBuilder builder = new StringBuilder(item.toString()).append(",\nNumber of stores supplied in: ")
                                                                  .append(storesCount)
                                                                  .append(",\nAverage price: ")
                                                                  .append(avgPrice)
                                                                  .append(",\nNumber of purchases: ")
                                                                  .append(ordersCount);
        return builder.toString();
    }
}
