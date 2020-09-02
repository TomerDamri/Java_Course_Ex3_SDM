package course.java.sdm.engine.exceptions;

import java.util.Set;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException (String itemName, String storeName) {
        super(String.format("The item: %s doesn't exist in store %s", itemName, storeName));
    }

    public ItemNotFoundException (String storeName, Set<Integer> nonExistingItems) {
        super(String.format("The item ids: %s in the store %s %s exist",
                            nonExistingItems,
                            storeName,
                            (nonExistingItems.size() > 1) ? "don't" : "doesn't"));
    }
}