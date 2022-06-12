package org.raado.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class StaticCommands {
    private final MongoCollection<Document> staticResourcesCollection;

    @Inject
    public StaticCommands(@Named("staticResourceCollectionName") final String staticResourceCollectionName, final MongoDatabase mdb) {
        this.staticResourcesCollection =  mdb.getCollection(staticResourceCollectionName);
    }
}
