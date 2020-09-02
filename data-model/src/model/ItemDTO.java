package model;

public class ItemDTO {
    private final int id;
    private final String name;
    private final String purchaseCategory;

    public ItemDTO(int id, String name, String purchaseCategory) {
        this.id = id;
        this.name = name;
        this.purchaseCategory = purchaseCategory;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPurchaseCategory() {
        return purchaseCategory;
    }
    @Override
    public String toString () {
        return "Id: " + id + ",\nName: " + name + ",\nPurchase Category: " + purchaseCategory;

    }
}
