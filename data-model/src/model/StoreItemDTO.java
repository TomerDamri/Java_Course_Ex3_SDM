package model;

public class StoreItemDTO {
    private final Integer id;
    private final String name;
    private final String purchaseCategory;
    private final Integer price;
    private final Double purchasesCount;

    public StoreItemDTO (Integer id, String name, String purchaseCategory, Integer price, Double purchasesCount) {
        this.id = id;
        this.name = name;
        this.purchaseCategory = purchaseCategory;
        this.price = price;
        this.purchasesCount = purchasesCount;
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

    public Integer getPrice () {
        return price;
    }

    public Double getPurchasesCount () {
        return purchasesCount;
    }
}
