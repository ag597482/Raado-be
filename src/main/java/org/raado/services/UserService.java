package org.raado.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.raado.commands.UserCommands;
import org.raado.models.Permission;
import org.raado.models.User;

import java.util.List;

@Slf4j
@Singleton
public class UserService {
    private final UserCommands userCommands;

    @Inject
    public UserService(final UserCommands userCommands) {
        this.userCommands = userCommands;
    }

    public User addUser(User user) {
         return userCommands.addUser(user);
    }

    public boolean updateUserPermissions(String userId, List<Permission> permissions) {
        return userCommands.updateUserPermissions(userId, permissions);
    }

    public List<User> getAllUsers() {
        return userCommands.getUsers();
    }

    public User validateUser(String phoneNo, String password) {
        return userCommands.validateAuth(phoneNo, password);
    }
}
