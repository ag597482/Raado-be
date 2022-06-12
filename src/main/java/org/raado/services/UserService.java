package org.raado.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.raado.commands.LocalCacheCommands;
import org.raado.commands.UserCommands;
import org.raado.models.Permission;
import org.raado.models.User;

import java.util.List;

@Slf4j
@Singleton
public class UserService {
    private final UserCommands userCommands;

    private final LocalCacheCommands localCacheCommands;

    @Inject
    public UserService(final UserCommands userCommands,
                       final LocalCacheCommands localCacheCommands) {
        this.userCommands = userCommands;
        this.localCacheCommands = localCacheCommands;
    }

    public User addUser(final User user) {
         final User newUser = userCommands.addUser(user);
         localCacheCommands.refreshUsersCache(newUser);
         return newUser;
    }

    public boolean updateUserPermissions(String userId, List<Permission> permissions) {
        boolean isSuccess = userCommands.updateUserPermissions(userId, permissions);
        if (isSuccess) {
            localCacheCommands.getAllUsers().get(userId).setPermissions(permissions);
        }
        return isSuccess;
    }

    public List<User> getAllUsers() {
        return localCacheCommands.getAllUsers().values().stream().toList();
        //userCommands.getUsers();
    }

    public User validateUser(final String phoneNo, final String password) {
        return userCommands.validateAuth(phoneNo, password);
    }
}
