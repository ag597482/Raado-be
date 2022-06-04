package org.raado.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoDatabaseImpl;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.codehaus.jackson.map.ObjectMapper;
import org.raado.models.Permission;
import org.raado.models.Transaction;
import org.raado.models.User;
import org.raado.utils.RaadoUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.reflections.Reflections.log;

public class UserCommands {

    private MongoCollection<Document> userCollection;

    RaadoUtils<User> raadoUtils;

    @Inject
    public UserCommands(@Named("userCollectionName") final String userCollectionName, final MongoDatabase mdb) {
        this.userCollection = mdb.getCollection(userCollectionName);
        raadoUtils = new RaadoUtils<User>();
    }

    public boolean addUser(final User user) {
        return userCollection.insertOne(raadoUtils.convertToDocument(user)).wasAcknowledged();
    }

    public boolean updateUserPermissions(final String phoneNo, final List<Permission> permissions) {
        final Document query = new Document().append("phoneNo",  phoneNo);
        Bson updates =
                Updates.set("permissions", permissions);
        UpdateOptions options = new UpdateOptions().upsert(true);


        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(userCollection.find().filter(query).iterator().next().toJson(), User.class);

//            User updatedUser = new User(user.getName(), user.getPhoneNo(), user.getPassword(), user.isAdmin(), permissions);
            //userCollection.updateOne(user, )
            UpdateResult result = userCollection.updateOne(Filters.eq("phoneNo", phoneNo), Updates.set("permissions", permissions));
            //userCollection.updateOne(query, updates);
            log.info("Modified document count: " + result.getModifiedCount());
            log.info("Upserted id: " + result.getUpsertedId()); // only contains a value when an upsert is performed
        } catch (Exception me) {
            System.err.println("Unable to update due to an error: " + me);
        }


//        BasicDBObject searchQuery = new BasicDBObject().append("phoneNo", phoneNo);
//        User user = jacksonDBCollection.find(searchQuery).next();
//        User user1 = new User(user);
//        user1.setPermissions(permissions);
//        final WriteResult writeResult = jacksonDBCollection.update(user, user1);
//        return writeResult.getError() == null;
        return true;
    }

    public List<User> getUsers() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final FindIterable<Document> iterable = userCollection.find();
        MongoCursor<Document> cursor = iterable.iterator();
        System.out.println("Student list with a cursor: ");
        ArrayList<User> users = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                //log.info(cursor.next().toJson());
                users.add(objectMapper.readValue(cursor.next().toJson(), User.class));
            }
        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        return users;
    }
}
