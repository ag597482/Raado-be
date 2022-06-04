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
    private UserCommands userCommands;

    @Inject
    public UserService(final UserCommands userCommands) {
        this.userCommands = userCommands;
    }

    public boolean addUser(User user) {
        return userCommands.addUser(user);
    }

    public boolean updateUserPermissions(String phoneNo, List<Permission> permissions) {
        return userCommands.updateUserPermissions(phoneNo, permissions);
    }

    public List<User> getAllUsers() {
        return userCommands.getUsers();
    }
}
