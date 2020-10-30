package course.java.sdm.engine.utils;

import java.util.Map;

import course.java.sdm.engine.model.SystemStore;
import course.java.sdm.engine.model.Zone;

public class SDMUtils {

    public static SystemStore getStoreByID (Zone zone, Integer storeId) {
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();
        if (!systemStores.containsKey(storeId)) {
            throw new RuntimeException(String.format("There is no store with id: '%s' in '%s' zone", storeId, zone.getZoneName()));
        }

        return systemStores.get(storeId);
    }

}
