package org.raado.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.raado.AppConfig;
import org.raado.configs.CacheConfig;
import org.raado.configs.MongoConfig;

import javax.inject.Named;
import java.util.ArrayList;

@Slf4j
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
    public MongoDatabase providesMongoDbGlobal(AppConfig appConfig)  {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://inventory-admin:inventory123@inventory.rbcanbq.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(appConfig.getMongoConfig().getMongodbName());
        ArrayList<String> databases = mongoClient.listDatabaseNames().into(new ArrayList<>());
        log.info("databases present are" + databases);
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

    @Provides
    @Singleton
    @Named("staticResourceCollectionName")
    public String providesStaticResourceName(AppConfig appConfig) {
        return appConfig.getStaticResourceCollectionName();
    }

    @Provides
    @Singleton
    public CacheConfig providesCacheConfig(AppConfig appConfig) {
        return appConfig.getCacheConfig();
    }
}
