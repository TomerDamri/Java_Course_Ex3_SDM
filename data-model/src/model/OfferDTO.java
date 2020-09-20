package model;

public class OfferDTO {

    private final Integer id;
    private final Double quantity;
    private final Integer itemId;
    private final Integer forAdditional;

    public OfferDTO(Integer id, Double quantity, Integer itemId, Integer forAdditional) {
        this.id = id;
        this.quantity = quantity;
        this.itemId = itemId;
        this.forAdditional = forAdditional;
    }

    public Integer getId() {
        return id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getForAdditional() {
        return forAdditional;
    }
}