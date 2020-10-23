package course.java.sdm.engine.utils.fileManager;

import course.java.sdm.engine.exceptions.DuplicateEntityException;
import course.java.sdm.engine.exceptions.IllegalFileExtensionException;
import course.java.sdm.engine.exceptions.ItemNotExist;
import course.java.sdm.engine.exceptions.NotFoundException;
import course.java.sdm.engine.model.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class FileManagerValidator {
    private static final String XML_EXTENSION = ".xml";

    public void validateFile (String filePath) {
        validateFileExtension(filePath);
    }

    public void validateItemsAndStores (Map<Integer, Item> items,
                                        Map<Integer, Store> stores,
                                        String zoneName,
                                        SDMDescriptor sdmDescriptor) {
        Set<Integer> itemsIds = items.keySet();
        Set<Integer> suppliedItemsIds = new HashSet<>();

        // validate that all the supplied items exist in items
        for (Store store : stores.values()) {
            Set<Integer> itemsInStoreIds = validateStoreItemsExist(itemsIds, store);
            suppliedItemsIds.addAll(itemsInStoreIds);
        }

        // validate that all items are supplied in at least 1 store
        validateAllItemsSupplied(itemsIds, suppliedItemsIds);

        // validate stores location are unique
        validateAllLocations(stores, sdmDescriptor.getSystemLocations());

        // validate unique zone name
        validateZoneName(sdmDescriptor.getZones(), zoneName);
    }

    private void validateZoneName (Map<String, Zone> zones, String newZoneName) {
        if (zones.containsKey(newZoneName)) {
            throw new RuntimeException(String.format("The zone name '%s' already exist in the system", newZoneName));
        }
    }

    private void validateAllLocations (Map<Integer, Store> stores, Map<Location, SystemStore> systemLocations) {
        Set<Location> allSystemLocations = new HashSet<>(systemLocations.keySet());

        validateLocations(stores.values(), Store::getLocation, allSystemLocations);
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