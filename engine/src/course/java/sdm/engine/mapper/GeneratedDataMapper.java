package course.java.sdm.engine.mapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import course.java.sdm.engine.exceptions.DuplicateEntityException;
import course.java.sdm.engine.model.*;
import course.java.sdm.engine.model.Location;
import examples.jaxb.schema.generated.*;

public class GeneratedDataMapper {

    public List<Customer> generatedCustomersToCustomers (SDMCustomers sdmCustomers) {
        if (sdmCustomers == null) {
            return null;
        }

        return sdmCustomers.getSDMCustomer().stream().map(this::toCustomer).collect(Collectors.toList());
    }

    public Map<Integer, Item> generatedItemsToItems (SDMItems generatedItems) {
        if (generatedItems == null) {
            return null;
        }

        return toItems(generatedItems);
    }

    public Map<Integer, Store> generatedStoresToStores (SDMStores generatedStores, Map<Integer, Item> items) {
        if (generatedStores == null) {
            return null;
        }
        return toStores(generatedStores, items);
    }

    public Descriptor toDescriptor (Map<Integer, Item> items, Map<Integer, Store> stores, List<Customer> customers) {
        ArrayList<Store> storesList = new ArrayList<>(stores.values());
        Map<Integer, SystemStore> systemStores = generatedListToMap(storesList,
                                                                    Store::getId,
                                                                    SystemStore::new,
                                                                    Store.class.getSimpleName());
        Map<Integer, SystemItem> systemItems = toSystemItems(items, systemStores.values());
        Map<Integer, SystemCustomer> systemCustomers = toSystemCustomers(customers);

        return new Descriptor(systemStores, systemItems, systemCustomers);
    }

    private Map<Integer, SystemItem> toSystemItems (Map<Integer, Item> items, Collection<SystemStore> stores) {
        Map<Integer, SystemItem> systemItems = new HashMap<>();

        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            Integer minPrice = null, storeSellsInCheapestPrice = null, storesCount = 0;
            double avgPrice = 0, sumPrices = 0;

            for (SystemStore store : stores) {
                Map<Integer, StoreItem> storeItems = store.getItemIdToStoreItem();
                if (storeItems.containsKey(entry.getKey())) {
                    int priceInStore = storeItems.get(entry.getKey()).getPrice();
                    if (minPrice == null || minPrice > priceInStore) {
                        minPrice = priceInStore;
                        storeSellsInCheapestPrice = store.getId();
                    }
                    storesCount++;
                    sumPrices += priceInStore;
                }
            }

            avgPrice = calculateAvgPrice(storesCount, avgPrice, sumPrices);
            SystemItem systemItem = toSystemItem(storesCount, avgPrice, entry, storeSellsInCheapestPrice);
            systemItems.put(entry.getKey(), systemItem);
        }

        return systemItems;
    }

    private Map<Integer, SystemCustomer> toSystemCustomers (List<Customer> customers) {
        return generatedListToMap(customers, Customer::getId, this::toSystemCustomer, Customer.class.getSimpleName());
    }

    private SystemCustomer toSystemCustomer (Customer customer) {
        return new SystemCustomer(customer);
    }

    private Customer toCustomer (SDMCustomer sdmCustomer) {
        return new Customer(sdmCustomer.getId(), sdmCustomer.getName(), toLocation(sdmCustomer.getLocation()));
    }

    private double calculateAvgPrice (int storesCount, double avgPrice, double sumPrices) {
        if (storesCount > 0) {
            avgPrice = round(sumPrices / storesCount, 2);
        }
        return avgPrice;
    }

    private SystemItem toSystemItem (int storesCount, double avgPrice, Map.Entry<Integer, Item> entry, int storeSellsInCheapestPrice) {
        SystemItem systemItem;
        systemItem = new SystemItem(entry.getValue());
        systemItem.setAvgPrice(avgPrice);
        systemItem.setStoresCount(storesCount);
        systemItem.setStoreSellsInCheapestPrice(storeSellsInCheapestPrice);
        return systemItem;
    }

    private Map<Integer, Store> toStores (SDMStores sdmStores, Map<Integer, Item> items) {
        if (sdmStores == null) {
            return null;
        }

        return generatedListToMap(sdmStores.getSDMStore(),
                                  SDMStore::getId,
                                  sdmStore -> toStore(sdmStore, items),
                                  SDMStore.class.getSimpleName());
    }

    private Location toLocation (examples.jaxb.schema.generated.Location generatedLocation) {
        if (generatedLocation == null) {
            return null;
        }

        return new Location(generatedLocation.getX(), generatedLocation.getY());
    }

    private Item toItem (SDMItem generatedItem) {
        if (generatedItem == null) {
            return null;
        }

        return new Item(generatedItem.getName(), generatedItem.getPurchaseCategory(), generatedItem.getId());
    }

    private Map<Integer, Item> toItems (SDMItems generatedItems) {
        if (generatedItems == null) {
            return null;
        }

        return generatedListToMap(generatedItems.getSDMItem(), SDMItem::getId, this::toItem, Item.class.getSimpleName());
    }

    private Store toStore (SDMStore generatedStore, Map<Integer, Item> items) {
        if (generatedStore == null) {
            return null;
        }

        return new Store(generatedStore.getName(),
                         generatedStore.getDeliveryPpk(),
                         toLocation(generatedStore.getLocation()),
                         toStoreItems(generatedStore.getSDMPrices(), items),
                         generatedStore.getId());
    }

    private Map<Integer, StoreItem> toStoreItems (SDMPrices sdmPrices, Map<Integer, Item> items) {
        return generatedListToMap(sdmPrices.getSDMSell(),
                                  SDMSell::getItemId,
                                  sdmSell -> toStoreItem(sdmSell, items),
                                  SDMSell.class.getSimpleName());
    }

    private StoreItem toStoreItem (SDMSell sdmSell, Map<Integer, Item> items) {
        if (sdmSell == null) {
            return null;
        }

        Item item = items.get(sdmSell.getItemId());
        if (item == null) {
            // throw new ItemNotFoundException();
        }

        return new StoreItem(item, sdmSell.getPrice());
    }

    private <K, V, G> Map<K, V> generatedListToMap (List<G> list,
                                                    Function<G, K> getKeyFunction,
                                                    Function<G, V> getValueFunction,
                                                    String valuesClassName) {
        try {
            Map<K, V> map = list.stream().filter(Objects::nonNull).collect(Collectors.toMap(getKeyFunction, getValueFunction));
            if (map.keySet().size() != list.size()) {
                // throw appropriate exception
            }

            return map;
        }
        catch (IllegalStateException ex) {
            throw new DuplicateEntityException(valuesClassName, ex);
        }
    }

    public static double round (double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
