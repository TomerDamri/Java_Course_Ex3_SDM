package course.java.sdm.engine.utils.fileManager;

import java.io.*;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import course.java.sdm.engine.exceptions.FileNotLoadedException;
import course.java.sdm.engine.exceptions.FileNotSaveException;
import course.java.sdm.engine.mapper.GeneratedDataMapper;
import course.java.sdm.engine.model.Descriptor;
import course.java.sdm.engine.model.Item;
import course.java.sdm.engine.model.Store;
import course.java.sdm.engine.model.SystemOrdersHistory;
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

    public SuperDuperMarketDescriptor generateDataFromXmlFile (String xml_file_path) throws FileNotFoundException {
        FILE_MANAGER_VALIDATOR.validateFile(xml_file_path);
        InputStream inputStream = new FileInputStream(new File(xml_file_path));
        SuperDuperMarketDescriptor superDuperMarketDescriptor = null;
        try {
            superDuperMarketDescriptor = deserializeFrom(inputStream);
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        return superDuperMarketDescriptor;
    }

    public Descriptor loadDataFromGeneratedData (SuperDuperMarketDescriptor superDuperMarketDescriptor) {
        Map<Integer, Item> items = GENERATED_DATA_MAPPER.generatedItemsToItems(superDuperMarketDescriptor.getSDMItems());
        Map<Integer, Store> stores = GENERATED_DATA_MAPPER.generatedStoresToStores(superDuperMarketDescriptor.getSDMStores(), items);
        FILE_MANAGER_VALIDATOR.validateItemsAndStores(items, stores);

        return GENERATED_DATA_MAPPER.toDescriptor(items, stores);
    }

    public void saveOrdersHistoryToFile (Descriptor descriptor, String path) {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }

        try {
            SystemOrdersHistory systemOrdersHistory = new SystemOrdersHistory(descriptor.getSystemOrders(), descriptor.getDynamicOrders());
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
}