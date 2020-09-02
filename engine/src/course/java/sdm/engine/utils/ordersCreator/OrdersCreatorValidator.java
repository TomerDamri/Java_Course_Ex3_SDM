package course.java.sdm.engine.utils.ordersCreator;

import java.util.Map;

import course.java.sdm.engine.exceptions.BadRequestException;
import course.java.sdm.engine.exceptions.ItemNotFoundException;
import course.java.sdm.engine.model.*;

public class OrdersCreatorValidator {

    public void validateLocation (Location orderLocation, SystemStore systemStore) {
        if (systemStore.getLocation().equals(orderLocation)) {
            throw new BadRequestException("Invalid location. It is the location of the selected store.");
        }
    }

    public void validateAmount (PricedItem pricedItem, Double amount) {
        validatePositiveAmount(amount);
        validateAmountToPurchaseCategory(pricedItem, amount);
    }

    public void validateItemExistsInStore (Item item, SystemStore systemStore) {
        Map<Integer, StoreItem> itemIdToStoreItemMap = systemStore.getItemIdToStoreItem();
        if (itemIdToStoreItemMap.get(item.getId()) == null) {
            throw new ItemNotFoundException(item.getName(), systemStore.getName());
        }
    }

    private void validateAmountToPurchaseCategory (PricedItem pricedItem, Double amount) {
        if (pricedItem.getPurchaseCategory().equals(Item.PurchaseCategory.QUANTITY)) {
            if (amount.intValue() < amount) {
                throw new IllegalArgumentException(String.format("Invalid amount.\nPurchase category for item id %s is quantity and the amount should be an integer.",
                                                                 pricedItem.getId()));
            }
        }
    }

    private void validatePositiveAmount (Double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid amount.\nAmount should be positive");
        }
    }
}