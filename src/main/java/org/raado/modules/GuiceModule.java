package org.raado.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoDatabaseImpl;
import org.raado.AppConfig;
import org.raado.configs.MongoConfig;

import javax.inject.Named;
import java.net.UnknownHostException;

import static org.reflections.Reflections.log;

public class GuiceModule extends AbstractModule {


    @Provides
    @Singleton
    public String providesDefaultName(AppConfig appConfig) {
        return appConfig.getDefaultName();
    }

    @Provides
    @Singleton
    public MongoConfig providesMongoConfig(AppConfig appConfig) {
        return appConfig.getMongoConfig();
    }

    @Provides
    @Singleton
    public DB providesMongoDb(AppConfig appConfig) throws UnknownHostException {
//        final Mongo mongo = new Mongo(appConfig.getMongoConfig().getMongohost(), appConfig.getMongoConfig().getMongoport());
//        return mongo.getDB(appConfig.getMongoConfig().getMongodbName());
        return null;
    }

    @Provides
    @Singleton
    public MongoDatabase providesMongoDbGlobal(AppConfig appConfig)  {
//        MongoClient mongoClient = MongoClients.create("mongodb+srv://raado:inventory123@realmcluster.e9nnz.mongodb.net/?retryWrites=true&w=majority");
//
//        return mongoClient.getDatabase("raado");

        ConnectionString connectionString = new ConnectionString("mongodb+srv://inventory-admin:inventory123@inventory.rbcanbq.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("inventory");
        log.info("aman123" + mongoDatabase.toString());
        log.info("aman123" + mongoDatabase.getCollection("transactions").find());
        return mongoDatabase;
    }

    @Provides
    @Singleton
    @Named("userCollectionName")
    public String providesUserCollectionName(AppConfig appConfig) {
        return appConfig.getUserCollectionName();
    }

    @Provides
    @Singleton
    @Named("transactionCollectionName")
    public String providesTransactionCollectionName(AppConfig appConfig) {
        return appConfig.getTransactionCollectionName();
    }
}
