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
import org.raado.models.User;
import org.raado.utils.RaadoUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Slf4j
public class UserCommands {

    private final MongoCollection<Document> userCollection;

    RaadoUtils<User> raadoUtils;

    @Inject
    public UserCommands(@Named("userCollectionName") final String userCollectionName, final MongoDatabase mdb) {
        this.userCollection = mdb.getCollection(userCollectionName);
        raadoUtils = new RaadoUtils<User>();
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
        String userId = "UR" + UUID.randomUUID();
        user.setUserId(userId);
        if(userCollection.insertOne(raadoUtils.convertToDocument(user)).wasAcknowledged()) {
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

    public boolean updateUserPermissions(final String userId, final List<Permission> permissions) {
        final Document query = new Document().append("userId",  userId);
        boolean successfulUpdate = false;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final User user = objectMapper.readValue(userCollection.find(query).iterator().next().toJson(), User.class);
            final User updatedUser = new User(user);
            updatedUser.setPermissions(permissions);
            UpdateResult result = userCollection.replaceOne(query, raadoUtils.convertToDocument(updatedUser));
            successfulUpdate = result.wasAcknowledged();
        } catch (Exception me) {
            log.error("Error while updating user permissions =>" + me);
            throw new RaadoException("Network error please try after sometime.",
                    ErrorCode.INTERNAL_ERROR);
        }
        return successfulUpdate;
    }

    public List<User> getUsers() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final FindIterable<Document> iterable = userCollection.find();
        MongoCursor<Document> cursor = iterable.iterator();
        System.out.println("Student list with a cursor: ");
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
        final List<User> allUsers = getUsers();
        final User result = allUsers.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst().orElse(null);
        if(Objects.isNull(result)) {
            throw new RaadoException("User not present.",
                    ErrorCode.INTERNAL_ERROR);
        }
        return result;
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