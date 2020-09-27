package course.java.sdm.engine.exceptions;

import java.util.Set;
import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException (UUID orderId) {
        super(String.format("There is no order with ID: %s", orderId));
    }

    public NotFoundException (String itemName, String storeName) {
        super(String.format("The item: %s doesn't exist in store %s", itemName, storeName));
    }

    public NotFoundException (String storeName, Set<Integer> nonExistingItems) {
        super(String.format("The item ids: %s in the store %s %s exist",
                            nonExistingItems,
                            storeName,
                            (nonExistingItems.size() > 1) ? "don't" : "doesn't"));
    }
}