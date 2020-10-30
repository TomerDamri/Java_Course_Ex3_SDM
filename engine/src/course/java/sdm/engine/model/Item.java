package course.java.sdm.engine.model;

import java.io.Serializable;

public class Item implements Serializable {

    private int id;
    private String name;
    private PurchaseCategory purchaseCategory;

    public Item (String name, String purchaseCategory, int id) {
        this.id = id;
        this.name = name;
        this.purchaseCategory = PurchaseCategory.createPurchaseCategory(purchaseCategory);
    }

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public PurchaseCategory getPurchaseCategory () {
        return purchaseCategory;
    }

    @Override
    public String toString () {
        return "Id: " + id + ",\nName: " + name + ",\nPurchase Category: " + purchaseCategory;

    }

    public enum PurchaseCategory {
        QUANTITY, WEIGHT;

        private static PurchaseCategory createPurchaseCategory (String purchaseCategoryStr) {
            switch (purchaseCategoryStr) {
            case "Quantity":
                return QUANTITY;
            case "Weight":
                return WEIGHT;
            }

            throw new IllegalArgumentException(String.format("purchase category should be %s or %s and not %s",
                                                             QUANTITY,
                                                             WEIGHT,
                                                             purchaseCategoryStr));
        }
    }

}
