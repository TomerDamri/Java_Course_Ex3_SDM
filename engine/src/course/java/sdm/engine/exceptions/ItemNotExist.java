package course.java.sdm.engine.exceptions;

import java.util.Set;

public class ItemNotExist extends RuntimeException {
    public ItemNotExist (Set<Integer> notSuppliedItems) {
        super(String.format("There are items that aren't supplied in any store: %s", notSuppliedItems));
    }

    public ItemNotExist (String discountName, int itemId) {
        super(String.format("In discount: %s the item with id: %s isn't supplied in his store", discountName, itemId));
    }

    public ItemNotExist (Integer notSuppliedItemId) {
        super(String.format("There is no such item in the system with id :%s", notSuppliedItemId));
    }
}