package org.raado.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import org.raado.exceptions.ErrorCode;
import org.raado.exceptions.RaadoException;
import org.raado.models.Permission;
import org.raado.models.ProcessName;
import org.raado.models.User;
import org.raado.utils.RaadoUtils;

import java.io.IOException;
import java.util.*;


@Slf4j
public class UserCommands {

    private final MongoCollection<Document> userCollection;

    @Inject
    public UserCommands(@Named("userCollectionName") final String userCollectionName, final MongoDatabase mdb) {
        this.userCollection = mdb.getCollection(userCollectionName);
    }

    public User addUser(final User user) {
        boolean validPhoneNumber = validatePhone(user);
        if (!validPhoneNumber) {
            throw new RaadoException("Account Already exists with the given phone number",
                    ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
        }
        if (!user.isAdmin()) {
            user.setPermissions(new ArrayList<>());
        }
        else {
            List<Permission> allPermissions = new ArrayList<>();
            for(ProcessName processName : ProcessName.values()) {
                Permission p = new Permission();
                p.setProcessName(processName);
                p.setWrite(true);
                p.setEntriesRate(new HashMap<>());
                allPermissions.add(p);
            }
            user.setPermissions(allPermissions);
        }
        String userId = "UR" + UUID.randomUUID();
        user.setUserId(userId);
        if(userCollection.insertOne(Objects.requireNonNull(RaadoUtils.<User>convertToDocument(user))).wasAcknowledged()) {
            return getUserById(userId);
        }
        throw new RaadoException("Network error please try after sometime.",
                ErrorCode.INTERNAL_ERROR);
    }

    private boolean validatePhone(User user) {
        final List<User> allUsers = getUsers();
        final List<User> samePhoneNumber = allUsers.stream()
                .filter(user1 -> user1.getPhoneNo().equals(user.getPhoneNo())).toList();
        return samePhoneNumber.size() <= 0;
    }

    public List<Permission> updateUserPermissions(final String userId, final List<Permission> permissions) {
        final Document query = new Document().append("userId",  userId);
        boolean successfulUpdate = false;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final User user = objectMapper.readValue(userCollection.find(query).iterator().next().toJson(), User.class);
            List<Permission> oldPermissions = user.getPermissions();
            Map<ProcessName, Map<String, Integer>> processWiseRates = new HashMap<>();
            oldPermissions.forEach(permission ->
                            processWiseRates.put(permission.getProcessName(), permission.getEntriesRate()));
            final User updatedUser = new User(user);
            permissions.forEach(permission -> {
                        Map<String, Integer> processRate = processWiseRates.containsKey(permission.getProcessName())
                                ? processWiseRates.get(permission.getProcessName()) : new HashMap<>();
                         permission.setEntriesRate(processRate);
                    });
            updatedUser.setPermissions(permissions);
            UpdateResult result = userCollection.replaceOne(query, Objects.requireNonNull(RaadoUtils.<User>convertToDocument(updatedUser)));
            successfulUpdate = result.wasAcknowledged();
            if(!successfulUpdate)
                return null;
        } catch (Exception me) {
            log.error("Error while updating user permissions =>" + me);
            throw new RaadoException("Network error please try after sometime.",
                    ErrorCode.INTERNAL_ERROR);
        }
        return permissions;
    }

    public List<Permission> updateUserPermissionRate(String userId, Permission processPermission) {
        final Document query = new Document().append("userId",  userId);
        List<Permission> oldPermissions;
        boolean successfulUpdate;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final User user = objectMapper.readValue(userCollection.find(query).iterator().next().toJson(), User.class);
            oldPermissions = user.getPermissions();
            oldPermissions
                    .forEach(permission -> {
                        if (permission.getProcessName().equals(processPermission.getProcessName())) {
                            permission.setEntriesRate(processPermission.getEntriesRate());
                        }
                    });
            final User updatedUser = new User(user);
            updatedUser.setPermissions(oldPermissions);
            UpdateResult result = userCollection.replaceOne(query, Objects.requireNonNull(RaadoUtils.<User>convertToDocument(updatedUser)));
            successfulUpdate = result.wasAcknowledged();
            if(!successfulUpdate)
                return null;
        } catch (Exception me) {
            log.error("Error while updating user permissions =>" + me);
            throw new RaadoException("Network error please try after sometime.",
                    ErrorCode.INTERNAL_ERROR);
        }
        return oldPermissions;
    }

    public List<User> getUsers() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final FindIterable<Document> iterable = userCollection.find();
        MongoCursor<Document> cursor = iterable.iterator();
        System.out.println("Getting the user's for the db");
        ArrayList<User> users = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                users.add(objectMapper.readValue(cursor.next().toJson(), User.class));
            }
        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        return users;
    }

    public User getUserById(final String userId) {
        final Document query = new Document().append("userId", userId);
        final ObjectMapper objectMapper = new ObjectMapper();
        User user = null;
        try {
            user = objectMapper.readValue(userCollection.find(query).iterator().next().toJson(), User.class);
        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        if(Objects.isNull(user)) {
            throw new RaadoException("User not present for userId : " + userId,
                    ErrorCode.INTERNAL_ERROR);
        }
        return user;
    }

    public User validateAuth(final String phoneNumber, final String password) {
        final List<User> allUsers = getUsers();
        final User result = allUsers.stream()
                .filter(user -> user.getPhoneNo().equals(phoneNumber) && user.getPassword().equals(password))
                .findFirst().orElse(null);
        if(Objects.isNull(result)) {
            throw new RaadoException("User Id or Password dose not match",
                    ErrorCode.USERID_OR_PASSWORD_IS_INCORRECT);
        }
        return result;
    }
}


//
//    Bson updates = Updates.set("permissions", permissions);
//    UpdateOptions options = new UpdateOptions().upsert(true);