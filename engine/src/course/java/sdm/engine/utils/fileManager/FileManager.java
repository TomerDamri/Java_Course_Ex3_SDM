package course.java.sdm.engine.utils.fileManager;

import course.java.sdm.engine.exceptions.FileNotLoadedException;
import course.java.sdm.engine.exceptions.FileNotSaveException;
import course.java.sdm.engine.mapper.GeneratedDataMapper;
import course.java.sdm.engine.model.*;
import examples.jaxb.schema.generated.SuperDuperMarketDescriptor;
import model.ItemToAddDTO;
import model.request.AddStoreToZoneRequest;

import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileManager {

    private final static String JAXB_XML_PACKAGE_NAME = "examples.jaxb.schema.generated";
    private final static GeneratedDataMapper GENERATED_DATA_MAPPER = new GeneratedDataMapper();
    private final static FileManagerValidator FILE_MANAGER_VALIDATOR = new FileManagerValidator();

    private static FileManager singletonFileManager = null;

    private FileManager () {
    }

    public static FileManager getFileManager () {
        if (singletonFileManager == null) {
            singletonFileManager = new FileManager();
        }

        return singletonFileManager;
    }

    public SuperDuperMarketDescriptor generateDataFromXmlFile (Part part) throws FileNotFoundException {
        FILE_MANAGER_VALIDATOR.validateFile(getFileName(part));
        SuperDuperMarketDescriptor superDuperMarketDescriptor = null;
        try {
            superDuperMarketDescriptor = deserializeFrom(part.getInputStream());
        }
        catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
        return superDuperMarketDescriptor;
    }

    public Zone loadDataFromGeneratedData (SuperDuperMarketDescriptor superDuperMarketDescriptor,
                                           StoresOwner storesOwner,
                                           SDMDescriptor sdmDescriptor) {
        Map<Integer, Item> items = GENERATED_DATA_MAPPER.generatedItemsToItems(superDuperMarketDescriptor.getSDMItems());
        Map<Integer, Store> stores = GENERATED_DATA_MAPPER.generatedStoresToStores(superDuperMarketDescriptor.getSDMStores(), items);
        String zoneName = GENERATED_DATA_MAPPER.getZoneName(superDuperMarketDescriptor.getSDMZone(), sdmDescriptor);
        FILE_MANAGER_VALIDATOR.validateItemsAndStores(items, stores, zoneName, sdmDescriptor);

        return GENERATED_DATA_MAPPER.toZone(items, stores, zoneName, storesOwner);
    }

    public void saveOrdersHistoryToFile (SDMDescriptor sdmDescriptor, String path) {
        try {
            List<ZoneOrdersHistory> zoneOrdersHistories = sdmDescriptor.getZones()
                                                                       .entrySet()
                                                                       .stream()
                                                                       .map(entry -> new ZoneOrdersHistory(entry.getKey(),
                                                                                                           entry.getValue()
                                                                                                                .getSystemOrders()))
                                                                       .collect(Collectors.toList());
            SystemOrdersHistory systemOrdersHistory = new SystemOrdersHistory(zoneOrdersHistories);

            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(systemOrdersHistory);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
        catch (FileNotFoundException ex) {
            throw new FileNotSaveException(String.format("Failed to save file : %s because the file not found", path));
        }
        catch (IOException ex) {
            throw new FileNotSaveException(String.format("Failed to save file : %s.\nError message: %s ", path, ex.getMessage()));
        }
    }

    public SystemOrdersHistory loadDataFromFile (String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            SystemOrdersHistory systemOrdersHistory = (SystemOrdersHistory) objectInputStream.readObject();
            objectInputStream.close();

            return systemOrdersHistory;
        }
        catch (FileNotFoundException ex) {
            throw new FileNotLoadedException(String.format("Failed to load file : %s because the file not found", path));
        }
        catch (ClassNotFoundException ex) {
            throw new FileNotLoadedException(String.format("Failed to load file : %s because the class %s not found",
                                                           path,
                                                           SystemOrdersHistory.class.getSimpleName()));
        }
        catch (IOException ex) {
            throw new FileNotLoadedException(ex.getMessage());
        }
    }

    public SystemStore addStoreToZone (StoresOwner storesOwner, Zone zone, Location newStoreLocation, AddStoreToZoneRequest request) {

        Map<Integer, StoreItem> storeItems = createStoreItems(zone, request.getStoreItems());
        Map<Integer, List<Discount>> storeDiscountsMap = new HashMap<>();

        Store newStore = new Store(request.getStoreName(),
                                   request.getDeliveryPpk(),
                                   newStoreLocation,
                                   storeItems,
                                   zone.getSystemStores().size() + 1,
                                   storeDiscountsMap);

        SystemStore newSystemStore = GENERATED_DATA_MAPPER.toSystemStore(storesOwner, newStore);
        // systemUpdater
        zone.getSystemStores().put(newSystemStore.getId(), newSystemStore);
        return newSystemStore;
    }

    private Map<Integer, StoreItem> createStoreItems (Zone zone, List<ItemToAddDTO> itemsToAdd) {
        Map<Integer, SystemItem> systemItems = zone.getSystemItems();

        Map<Integer, StoreItem> newStoreItems = itemsToAdd.stream()
                                                          .filter(itemToAddDTO -> systemItems.containsKey(itemToAddDTO.getId()))
                                                          .map(itemToAddDTO -> {
                                                              Item item = systemItems.get(itemToAddDTO.getId()).getItem();

                                                              return new StoreItem(item, itemToAddDTO.getPrice());
                                                          })
                                                          .collect(Collectors.toMap(StoreItem::getId, storeItem -> storeItem));

        if (newStoreItems.size() != itemsToAdd.size()) {
            throw new RuntimeException(String.format("You entered an item id that not exist in the items collection of '%s' zone",
                                                     zone.getZoneName()));
        }

        return newStoreItems;
    }

    private SuperDuperMarketDescriptor deserializeFrom (InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (SuperDuperMarketDescriptor) u.unmarshal(in);
    }

    private String getFileName (Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }
}