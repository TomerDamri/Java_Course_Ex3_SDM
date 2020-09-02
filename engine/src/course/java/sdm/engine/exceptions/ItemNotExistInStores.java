package course.java.sdm.engine.exceptions;

import java.util.Set;

public class ItemNotExistInStores extends RuntimeException {
    public ItemNotExistInStores (Set<Integer> notSuppliedItems) {
        super(String.format("There are items that aren't supplied in any store: %s", notSuppliedItems));
    }
}