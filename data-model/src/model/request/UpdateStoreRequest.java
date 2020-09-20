package model.request;

public class UpdateStoreRequest extends BaseUpdateStoreRequest {
    private final Integer itemPrice;

    public UpdateStoreRequest (Integer storeId, Integer itemId, Integer itemPrice) {
        super(storeId, itemId);
        this.itemPrice = itemPrice;
    }

    public Integer getItemPrice () {
        return itemPrice;
    }
}