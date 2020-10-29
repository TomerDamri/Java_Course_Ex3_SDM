package model.request;

import java.util.List;
import java.util.UUID;

import model.ChosenDiscountDTO;

public class AddDiscountsToOrderRequest {
    private UUID orderId;
    private List<ChosenDiscountDTO> chosenDiscounts;

    public AddDiscountsToOrderRequest (UUID orderId, List<ChosenDiscountDTO> chosenDiscounts) {
        this.orderId = orderId;
        this.chosenDiscounts = chosenDiscounts;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public List<ChosenDiscountDTO> getChosenDiscounts () {
        return chosenDiscounts;
    }
}
