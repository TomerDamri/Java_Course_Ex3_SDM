package model.response;

public class ItemSummaryForOrder {

    public static String QUANTITY = "QUANTITY";
    public static String WEIGHT = "WEIGHT";

    private final Integer id;
    private final String name;
    private final String purchaseCategory;
    private final Double quantity;
    private final Integer unitPrice;
    private final Double totalPrice;
    private final Boolean isPurchasedAsPartOfDiscount;

    public ItemSummaryForOrder (Integer id,
                                String name,
                                String purchaseCategory,
                                Double quantity,
                                Integer unitPrice,
                                Double totalPrice,
                                Boolean isPurchasedAsPartOfDiscount) {
        this.id = id;
        this.name = name;
        this.purchaseCategory = purchaseCategory;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.isPurchasedAsPartOfDiscount = isPurchasedAsPartOfDiscount;
    }

    public static String getQUANTITY () {
        return QUANTITY;
    }

    public static String getWEIGHT () {
        return WEIGHT;
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

    public Double getQuantity () {
        return quantity;
    }

    public Integer getUnitPrice () {
        return unitPrice;
    }

    public Double getTotalPrice () {
        return totalPrice;
    }

    public Boolean getPurchasedAsPartOfDiscount () {
        return isPurchasedAsPartOfDiscount;
    }

    @Override
    public String toString () {
        return new StringBuilder("id= ").append(id)
                                        .append(",\n name= '")
                                        .append(name)
                                        .append('\'')
                                        .append(",\n purchase Category= '")
                                        .append(purchaseCategory)
                                        .append('\'')
                                        .append(",\n quantity= ")
                                        .append(quantity)
                                        .append(",\n unit Price=")
                                        .append(unitPrice)
                                        .append(",\n total Price= ")
                                        .append(totalPrice)
                                        .append(",\n is Purchased As Part Of Discount= ")
                                        .append(isPurchasedAsPartOfDiscount)
                                        .append('}')
                                        .toString();
    }
}
