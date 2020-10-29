package model.request;

import java.util.List;
import java.util.UUID;

import model.ChosenDiscountDTO;

public class AddDiscountsToOrderRequest {
    private UUID orderID;
    private List<ChosenDiscountDTO> chosenDiscounts;

    public AddDiscountsToOrderRequest (UUID orderID, List<ChosenDiscountDTO> chosenDiscounts) {
        this.orderID = orderID;
        this.chosenDiscounts = chosenDiscounts;
    }

    public UUID getOrderID () {
        return orderID;
    }

    public List<ChosenDiscountDTO> getChosenDiscounts () {
        return chosenDiscounts;
    }
}
