package model;

import java.util.List;

public class ValidItemDiscountsDTO {

    private Integer itemId;
    private String itemName;
    private List<DiscountDTO> validDiscounts;

    public ValidItemDiscountsDTO (Integer itemId, String itemName, List<DiscountDTO> validDiscounts) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.validDiscounts = validDiscounts;
    }

    public Integer getItemId () {
        return itemId;
    }

    public void setItemId (Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName () {
        return itemName;
    }

    public void setItemName (String itemName) {
        this.itemName = itemName;
    }

    public List<DiscountDTO> getValidDiscounts () {
        return validDiscounts;
    }

    public void setValidDiscounts (List<DiscountDTO> validDiscounts) {
        this.validDiscounts = validDiscounts;
    }
}
