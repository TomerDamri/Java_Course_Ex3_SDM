package model;

public class StoreItemDTO {
    private final PricedItemDTO pricedItem;
    private final Double purchasesCount;

    public StoreItemDTO (PricedItemDTO pricedItem, Double purchasesCount) {
        this.pricedItem = pricedItem;
        this.purchasesCount = purchasesCount;
    }

    public PricedItemDTO getPricedItem() {
        return pricedItem;
    }

    public int getId () {
        return pricedItem.getId();
    }

    public String getName () {
        return pricedItem.getName();
    }

    public String getPurchaseCategory () {
        return pricedItem.getPurchaseCategory();
    }

    public int getPrice () {
        return pricedItem.getPrice();
    }

    public Double getPurchasesCount () {
        return purchasesCount;
    }

    @Override
    public String toString () {
        return new StringBuilder(pricedItem.toString()).append("\nNumber of purchases in store: ").append(purchasesCount).toString();
    }
}
