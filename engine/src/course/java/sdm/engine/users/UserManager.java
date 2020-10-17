package course.java.sdm.engine.users;

import course.java.sdm.engine.model.SDMDescriptor;
import course.java.sdm.engine.model.StoresOwner;
import course.java.sdm.engine.model.SystemCustomer;
import course.java.sdm.engine.model.SystemUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private static UserManager singletonUserManager = null;
    private final Map<String, SystemUser> nameToUserMap;

    public static UserManager getUserManager() {
        if (singletonUserManager == null) {
            singletonUserManager = new UserManager();
        }

        return singletonUserManager;
    }

    public void addUser(SDMDescriptor sdmDescriptor, String username, User.UserType userType) {
        UUID id = UUID.randomUUID();

        addUserToSDMDescriptor(sdmDescriptor, id, username, userType);
        addUserToManagerCollection(id, username, userType);
    }

    public SystemUser removeUser(SDMDescriptor sdmDescriptor, String username) {
        if (!isUserExists(username)) {
            throw new RuntimeException(String.format("There is no user with name: '%s' in system.\nThe remove operation failed", username));
        }

        SystemUser removedSystemUser = removeUserFromManagerCollection(username);
        removeUserFromSDMDescriptor(sdmDescriptor, removedSystemUser);

        return removedSystemUser;
    }

    public synchronized Collection<SystemUser> getUsers() {
        return nameToUserMap.values();
    }

    public boolean isUserExists(String username) {
        return nameToUserMap.containsKey(username);
    }


    private UserManager() {
        nameToUserMap = new HashMap<>();
    }

    private void addUserToSDMDescriptor(SDMDescriptor sdmDescriptor, UUID id, String userName, User.UserType userType) {
        if (userType.equals(User.UserType.CUSTOMER)) {
            addSystemCustomer(sdmDescriptor.getSystemCustomers(), id, userName);
        } else {
            addStoreOwner(sdmDescriptor.getStoresOwners(), id, userName);
        }
    }

    private synchronized void addSystemCustomer(Map<UUID, SystemCustomer> idToSystemCustomerMap, UUID id, String userName) {
        SystemCustomer newCustomer = new SystemCustomer(id, userName);

        idToSystemCustomerMap.put(id, newCustomer);
    }

    private synchronized void addStoreOwner(Map<UUID, StoresOwner> idToStoreOwnerMap, UUID id, String userName) {
        StoresOwner newStoreOwner = new StoresOwner(id, userName);

        idToStoreOwnerMap.put(id, newStoreOwner);
    }

    private synchronized void addUserToManagerCollection(UUID id, String userName, User.UserType userType) {
        SystemUser newSystemUser = new SystemUser(id, userName, toUserType(userType));

        nameToUserMap.put(newSystemUser.getName(), newSystemUser);
    }

    private synchronized SystemUser removeUserFromManagerCollection(String userName) {
        return nameToUserMap.remove(userName);
    }

    private void removeUserFromSDMDescriptor(SDMDescriptor sdmDescriptor, SystemUser systemUser) {
        UUID userId = systemUser.getId();
        String username = systemUser.getName();

        if (systemUser.getUserType().equals(SystemUser.UserType.CUSTOMER)) {
            removeCustomer(sdmDescriptor.getSystemCustomers(), userId, username);
        } else {
            removeStoreOwner(sdmDescriptor.getStoresOwners(), userId, username);
        }
    }

    private void removeCustomer(Map<UUID, SystemCustomer> systemCustomers, UUID userId, String username) {
        if (!systemCustomers.containsKey(userId)) {
            throw new RuntimeException(String.format("There is no customer with name: '%s' in system.\nThe remove operation failed", username));
        }

        removeSystemCustomer(systemCustomers, userId);
    }

    private synchronized SystemCustomer removeSystemCustomer(Map<UUID, SystemCustomer> idToSystemCustomerMap, UUID id) {
        return idToSystemCustomerMap.remove(id);
    }

    private void removeStoreOwner(Map<UUID, StoresOwner> storesOwners, UUID userId, String username) {
        if (!storesOwners.containsKey(userId)) {
            throw new RuntimeException(String.format("There is no store owner with name: '%s' in system.\nThe remove operation failed", username));
        }

        removeStoreOwner(storesOwners, userId);
    }

    private synchronized StoresOwner removeStoreOwner(Map<UUID, StoresOwner> idToStoreOwnerMap, UUID id) {
        return idToStoreOwnerMap.remove(id);
    }

    private SystemUser.UserType toUserType(User.UserType type) {
        return type.equals(User.UserType.CUSTOMER) ? SystemUser.UserType.CUSTOMER : SystemUser.UserType.STORE_OWNER;
    }
}
