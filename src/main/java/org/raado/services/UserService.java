package org.raado.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import org.raado.commands.LocalCacheCommands;
import org.raado.commands.UserCommands;
import org.raado.exceptions.ErrorCode;
import org.raado.exceptions.RaadoException;
import org.raado.models.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        List<Permission> updateUserPermissions = userCommands.updateUserPermissions(userId, permissions);
        if (Objects.nonNull(updateUserPermissions)) {
            localCacheCommands.getAllUsers().get(userId).setPermissions(updateUserPermissions);
        }
        return Objects.nonNull(updateUserPermissions);
    }

    public boolean updateUserProcessRate(String userId, ProcessName processName, Map<String, Integer> entriesRate) {
        Permission processPermission = getUserByIdOrPhoneNo(userId, null).getPermissions().stream()
                .filter(permission -> permission.getProcessName().equals(processName) && permission.isWrite())
                .findFirst().orElse(null);
        if (processPermission == null) {
            throw new RaadoException("Please provide write process for permissions to set the rate.",
                    ErrorCode.INTERNAL_ERROR);
        }
        processPermission.setEntriesRate(entriesRate);
        List<Permission> updateUserPermissions = userCommands.updateUserPermissionRate(userId, processPermission);
        if (Objects.nonNull(updateUserPermissions)) {
            localCacheCommands.getAllUsers().get(userId).setPermissions(updateUserPermissions);
        }
        return Objects.nonNull(updateUserPermissions);
    }

    public List<User> getAllUsers() {
        return localCacheCommands.getAllUsers().values().stream().toList();
        //userCommands.getUsers();
    }

    public User validateUser(final String phoneNo, final String password) {
        return userCommands.validateAuth(phoneNo, password);
    }

    public User getUserByIdOrPhoneNo(final String userId, final String phoneNo) {
        User user = null;
        if (Objects.nonNull(userId))
            user = localCacheCommands.getAllUsers().get(userId);
        else
            user = localCacheCommands.getAllUsers().values()
                    .stream()
                    .filter(savedUser -> savedUser.getPhoneNo().equals(phoneNo))
                    .findFirst()
                    .orElse(null);
        if (Objects.isNull(user)) {
            throw new RaadoException("User with give phone no. or userId is not present!",
                    ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    public  Map<ProcessName, Map<String, Integer>> getUserRates(final String userId) {
        Map<ProcessName, Map<String, Integer>> rates = new HashMap<>();
        User user = getUserByIdOrPhoneNo(userId, null);
        user.getPermissions()
                .forEach(permission -> rates.put(permission.getProcessName(), permission.getEntriesRate()));
        return rates;
    }
}
