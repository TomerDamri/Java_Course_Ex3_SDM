package course.java.sdm.engine.users;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private final Set<User> usersSet;
    private final Set<String> usersNamesSet;
    private int userId = 0;

    public UserManager() {
        usersSet = new HashSet<>();
        usersNamesSet = new HashSet<>();
    }

    public synchronized void addUser(String username, User.UserType userType) {
        usersSet.add(new User(++userId, username, userType));
        usersNamesSet.add(username);
    }

    public synchronized void removeUser(String username) {
        usersSet.remove(username);
    }

    public synchronized Set<User> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        return usersNamesSet.contains(username);
    }
}
