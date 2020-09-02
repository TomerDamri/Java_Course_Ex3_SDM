package model;

public class PricedItemDTO {
    private final ItemDTO item;
    private final int price;

    public PricedItemDTO(ItemDTO item, int price) {
        this.item = item;
        this.price = price;
    }
    public int getId() {
        return item.getId();
    }

    public String getName() {
        return item.getName();
    }

    public String getPurchaseCategory() {
        return item.getPurchaseCategory();
    }

    public ItemDTO getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString () {
        return new StringBuilder(item.toString()).append("\nItem price: ").append(price).toString();
    }
}
