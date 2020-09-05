package course.java.sdm.engine.exceptions;

import java.util.Set;

public class ItemNotExistInStores extends RuntimeException {
    public ItemNotExistInStores (Set<Integer> notSuppliedItems) {
        super(String.format("There are items that aren't supplied in any store: %s", notSuppliedItems));
    }

    public ItemNotExistInStores (String discountName, int itemId) {
        super(String.format("In discount: %s the item with id: %s isn't supplied in his store", discountName, itemId));
    }
}