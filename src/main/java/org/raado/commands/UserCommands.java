package org.raado.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoDatabaseImpl;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import org.bson.Document;
import org.raado.models.Permission;
import org.raado.models.User;

import java.util.ArrayList;
import java.util.List;

import static org.reflections.Reflections.log;

public class UserCommands {

    private JacksonDBCollection<User, String> jacksonDBCollection;

    @Inject
    public UserCommands(@Named("userCollectionName") final String userCollectionName, final MongoDatabase mdb) {
        //this.jacksonDBCollection = JacksonDBCollection.wrap(db.getCollection(userCollectionName), User.class, String.class);
        log.info("aman" + mdb.toString());
        MongoCollection<Document> mongoCollection = mdb.getCollection("transactions");
        log.info("aman" + mongoCollection.toString());
        //this.jacksonDBCollection = JacksonDBCollection.wrap(dbCollection, User.class, String.class);
    }

    public boolean addUser(final User user) {
        final WriteResult writeResult = jacksonDBCollection.insert(user);
        log.info( "records inserted" + writeResult.getN() + " " + writeResult.getError());
        return writeResult.getError() == null;
    }

    public boolean updateUserPermissions(final String phoneNo, final List<Permission> permissions) {
        BasicDBObject searchQuery = new BasicDBObject().append("phoneNo", phoneNo);
        User user = jacksonDBCollection.find(searchQuery).next();
        User user1 = new User(user);
        user1.setPermissions(permissions);
        final WriteResult writeResult = jacksonDBCollection.update(user, user1);
        return writeResult.getError() == null;
    }

    public List<User> getUsers() {
        DBCursor<User> dbCursor = jacksonDBCollection.find();
        List<User> datas = new ArrayList<>();
        while (dbCursor.hasNext()) {
            User data = dbCursor.next();
            datas.add(data);
        }
        return datas;
    }
}
