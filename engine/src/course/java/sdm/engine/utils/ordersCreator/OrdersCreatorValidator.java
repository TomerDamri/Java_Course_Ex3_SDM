package course.java.sdm.engine.utils.ordersCreator;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import course.java.sdm.engine.exceptions.BadRequestException;
import course.java.sdm.engine.exceptions.NotFoundException;
import course.java.sdm.engine.model.*;

public class OrdersCreatorValidator {
    public void validateOffersForOneOfOperator (Map<Integer, Offer> discountOffers, Optional<Integer> chosenOfferId, String discountName) {
        if (chosenOfferId == null || !chosenOfferId.isPresent()) {
            throw new RuntimeException(String.format("For discount: %s, you need to choose one possible offer (choose an Id between 1-%s)",
                                                     discountName,
                                                     discountOffers.size()));
        }

        if (!discountOffers.containsKey(chosenOfferId.get())) {
            throw new RuntimeException(String.format("For discount: %s, the offer id: %s that you sent is not valid, you need to choose an Id from 1-%s",
                                                     discountName,
                                                     chosenOfferId,
                                                     discountOffers.size()));
        }
    }

    public void validateOffersForAllOrNothingOperator (Integer discountOffersSize, String discountName) {
        if (discountOffersSize <= 0) {
            throw new RuntimeException(String.format("The operator 'ALL-OR-NOTHING ' of discount :'%s' is invalid because there are %s offers instead some offers",
                                                     discountName,
                                                     discountOffersSize));
        }
    }

    public void validateOffersForIrrelevantOperator (Integer discountOffersSize, String discountName) {
        if (discountOffersSize != 1) {
            throw new RuntimeException(String.format("The operator 'IRRELEVANT' of discount :'%s' is invalid because there are %s offers instead of one offer",
                                                     discountName,
                                                     discountOffersSize));
        }
    }

    public void validateNumOfRealizationForChosenDiscount (Double itemQuantityInOrder,
                                                           Double itemQuantityInDiscount,
                                                           String discountName,
                                                           int numOfRealization) {
        double totalNumOfChosenItemToAdd = itemQuantityInDiscount * numOfRealization;
        if (totalNumOfChosenItemToAdd > itemQuantityInOrder) {
            Double validNumOfRealization = (itemQuantityInOrder / itemQuantityInDiscount);
            throw new RuntimeException(String.format("You can implements the discount '%s' only %s times (and not %s times as you asked).",
                                                     discountName,
                                                     validNumOfRealization.intValue(),
                                                     numOfRealization));
        }
    }

    public void validateExistenceOfChosenDiscount (Map<String, Discount> discountNameToValidDiscount, UUID orderId, String discountName) {
        if (!discountNameToValidDiscount.containsKey(discountName)) {
            throw new RuntimeException(String.format("The chosen discount: '%s' is not exist in valid discounts for order: '%s'",
                                                     discountName,
                                                     orderId));
        }
    }

    public void validateTempStaticOrderExist (UUID orderId, Map<UUID, TempOrder> tempOrders) {
        if (!tempOrders.containsKey(orderId)) {
            throw new NotFoundException(orderId);
        }
    }

    public void validateLocation (Location orderLocation, SystemStore systemStore) {
        if (systemStore.getLocation().equals(orderLocation)) {
            throw new BadRequestException("Invalid location. It is the location of the selected store.");
        }
    }

    public void validateAmount (Item item, Double amount) {
        validatePositiveAmount(amount);
        validateAmountToPurchaseCategory(item, amount);
    }

    public void validateItemExistsInStore (Item item, SystemStore systemStore) {
        Map<Integer, StoreItem> itemIdToStoreItemMap = systemStore.getItemIdToStoreItem();
        if (itemIdToStoreItemMap.get(item.getId()) == null) {
            throw new NotFoundException(item.getName(), systemStore.getName());
        }
    }

    private void validateAmountToPurchaseCategory (Item item, Double amount) {
        if (item.getPurchaseCategory().equals(Item.PurchaseCategory.QUANTITY)) {
            if (amount.intValue() < amount) {
                throw new IllegalArgumentException(String.format("Invalid amount.\nPurchase category for item id %s is quantity and the amount should be an integer.",
                                                                 item.getId()));
            }
        }
    }

    private void validatePositiveAmount (Double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid amount.\nAmount should be positive");
        }
    }
}