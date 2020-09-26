package model;

public class PricedItemDTO {
    public static String QUANTITY = "QUANTITY";
    public static String WEIGHT = "WEIGHT";

    private final Integer id;
    private final String name;
    private final String purchaseCategory;
    private final Integer price;

    public PricedItemDTO (Integer id, String name, String purchaseCategory, int price) {
        this.id = id;
        this.name = name;
        this.purchaseCategory = purchaseCategory;
        this.price = price;
    }

    public Integer getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public String getPurchaseCategory () {
        return purchaseCategory;
    }

    public int getPrice () {
        return price;
    }
}
