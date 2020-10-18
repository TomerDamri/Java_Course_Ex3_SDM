package course.java.sdm.engine.utils.fileManager;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import course.java.sdm.engine.exceptions.FileNotLoadedException;
import course.java.sdm.engine.exceptions.FileNotSaveException;
import course.java.sdm.engine.mapper.GeneratedDataMapper;
import course.java.sdm.engine.model.*;
import examples.jaxb.schema.generated.SuperDuperMarketDescriptor;

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

    // public SuperDuperMarketDescriptor generateDataFromXmlFile (String xml_file_path) throws
    // FileNotFoundException {
    // FILE_MANAGER_VALIDATOR.validateFile(xml_file_path);
    // InputStream inputStream = new FileInputStream(new File(xml_file_path));
    // SuperDuperMarketDescriptor superDuperMarketDescriptor = null;
    // try {
    // superDuperMarketDescriptor = deserializeFrom(inputStream);
    // }
    // catch (JAXBException e) {
    // e.printStackTrace();
    // }
    // return superDuperMarketDescriptor;
    // }

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

    public Zone loadDataFromGeneratedData (SuperDuperMarketDescriptor superDuperMarketDescriptor, StoresOwner storesOwner) {
        Map<Integer, Item> items = GENERATED_DATA_MAPPER.generatedItemsToItems(superDuperMarketDescriptor.getSDMItems());
        // TODO: 02/09/2020 - add discounts to stores
        Map<Integer, Store> stores = GENERATED_DATA_MAPPER.generatedStoresToStores(superDuperMarketDescriptor.getSDMStores(), items);
        String zoneName = GENERATED_DATA_MAPPER.generatedZoneToZone(superDuperMarketDescriptor.getSDMZone());
        FILE_MANAGER_VALIDATOR.validateItemsAndStores(items, stores);

        return GENERATED_DATA_MAPPER.toZone(items, stores, zoneName, storesOwner);
    }

    public void saveOrdersHistoryToFile (SDMDescriptor sdmDescriptor, String path) {
        if (sdmDescriptor == null) {
            throw new FileNotLoadedException();
        }

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