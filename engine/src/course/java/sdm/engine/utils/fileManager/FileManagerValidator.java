package course.java.sdm.engine.utils.fileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;

import course.java.sdm.engine.exceptions.DuplicateEntityException;
import course.java.sdm.engine.exceptions.IllegalFileExtensionException;
import course.java.sdm.engine.exceptions.ItemNotExist;
import course.java.sdm.engine.exceptions.NotFoundException;
import course.java.sdm.engine.model.Customer;
import course.java.sdm.engine.model.Item;
import course.java.sdm.engine.model.Location;
import course.java.sdm.engine.model.Store;

public class FileManagerValidator {
    private static final String XML_EXTENSION = ".xml";

    public void validateFile (String filePath) throws FileNotFoundException {
        validateFileExtension(filePath);
        validateFileExists(filePath);
    }

    public void validateItemsAndStores (Map<Integer, Item> items, Map<Integer, Store> stores, List<Customer> customers) {
        Set<Integer> itemsIds = items.keySet();
        Set<Integer> suppliedItemsIds = new HashSet<>();

        // validate that all the supplied items exist in items
        for (Store store : stores.values()) {
            Set<Integer> itemsInStoreIds = validateStoreItemsExist(itemsIds, store);
            suppliedItemsIds.addAll(itemsInStoreIds);
        }

        // validate that all items are supplied in at least 1 store
        validateAllItemsSupplied(itemsIds, suppliedItemsIds);

        // validate all customers and stores location are unique
        validateAllLocations(stores, customers);
    }

    private Set<Location> validateAllLocations (Map<Integer, Store> stores, List<Customer> customers) {
        Set<Location> allSystemLocations = new HashSet<>();

        validateLocations(stores.values(), Store::getLocation, allSystemLocations);
        validateLocations(customers, Customer::getLocation, allSystemLocations);

        return allSystemLocations;
    }

    private <T> void validateLocations (Collection<T> entityCollection,
                                        Function<T, Location> getLocationFunction,
                                        Set<Location> allLocations) {
        entityCollection.stream().map(getLocationFunction).forEach(location -> addLocationToLocationSet(allLocations, location));
    }

    private void addLocationToLocationSet (Set<Location> allSystemLocations, Location location) {
        if (allSystemLocations.contains(location)) {
            throw new DuplicateEntityException(location);
        }

        allSystemLocations.add(location);
    }

    private void validateFileExists (String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("The file : %s doesn't exist", filePath));
        }
    }

    private Set<Integer> validateStoreItemsExist (Set<Integer> itemsIds, Store store) {
        Set<Integer> itemsInStoreIds = store.getItemIdToStoreItem().keySet();
        if (!itemsIds.containsAll(itemsInStoreIds)) {
            itemsInStoreIds.removeAll(itemsIds);
            throw new NotFoundException(store.getName(), itemsInStoreIds);
        }

        return itemsInStoreIds;
    }

    private void validateAllItemsSupplied (Set<Integer> items, Set<Integer> suppliedItems) {
        if (!items.equals(suppliedItems)) {
            items.removeAll(suppliedItems);
            throw new ItemNotExist(items);
        }
    }

    private void validateFileExtension (String dataPath) {
        if (!dataPath.endsWith(FileManagerValidator.XML_EXTENSION)) {
            throw new IllegalFileExtensionException(dataPath, FileManagerValidator.XML_EXTENSION);
        }
    }
}