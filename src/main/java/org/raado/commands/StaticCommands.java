package org.raado.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import org.raado.exceptions.ErrorCode;
import org.raado.exceptions.RaadoException;
import org.raado.models.*;
import org.raado.utils.RaadoUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
public class StaticCommands {
    private final MongoCollection<Document> staticResourcesCollection;

    @Inject
    public StaticCommands(@Named("staticResourceCollectionName") final String staticResourceCollectionName, final MongoDatabase mdb) {
        this.staticResourcesCollection =  mdb.getCollection(staticResourceCollectionName);
    }

    public  Map<ProcessName, Map<String, Integer>> getGlobalRates() {
        ProcessWiseConstants globalRates = new ProcessWiseConstants();
        try {
            final Document query = new Document().append("namespace", Constants.GLOBAL_RATES);
            final ObjectMapper objectMapper = new ObjectMapper();
            globalRates = objectMapper.readValue(staticResourcesCollection.find(query).iterator().next().toJson(), ProcessWiseConstants.class);

        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        return globalRates.getRates();
    }

    public  Map<ProcessName, Map<String, Integer>> getGlobalStock() {
        ProcessWiseConstants globalStocks = new ProcessWiseConstants();
        try {
            final Document query = new Document().append("namespace", Constants.GLOBAL_STOCK);
            final ObjectMapper objectMapper = new ObjectMapper();
            globalStocks = objectMapper.readValue(staticResourcesCollection.find(query).iterator().next().toJson(), ProcessWiseConstants.class);

        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        return globalStocks.getRates();
    }

    public Boolean updateGlobalProcessWiseConstants(final String namespace,
                                                    final ProcessName processName,
                                                    final Map<String, Integer> entries) {
        final Document query = new Document().append("namespace", namespace);
        boolean successfulUpdate = false;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final ProcessWiseConstants globalRates = objectMapper.readValue(staticResourcesCollection.find(query).iterator().next().toJson(), ProcessWiseConstants.class);
            globalRates.getRates().put(processName, entries);
            successfulUpdate = staticResourcesCollection
                    .replaceOne(query, Objects.requireNonNull(RaadoUtils.<ProcessWiseConstants>convertToDocument(globalRates))).wasAcknowledged();
           if(!successfulUpdate)
                return null;
        } catch (Exception me) {
            log.error("Error while updating user permissions =>" + me);
            throw new RaadoException("Network error please try after sometime.",
                    ErrorCode.INTERNAL_ERROR);
        }
        return successfulUpdate;
    }

    public boolean initializeGlobalRates() {
        final Document query = new Document().append("namespace", Constants.GLOBAL_RATES);
        final Map<ProcessName, Map<String, Integer>> initialGlobalRates = new HashMap<>();
        Arrays.stream(ProcessName.values()).sequential()
                .forEach(processName -> initialGlobalRates.put(processName, new HashMap<>()));
        final ProcessWiseConstants globalRates = ProcessWiseConstants.builder().namespace(Constants.GLOBAL_RATES).rates(initialGlobalRates).build();
        final UpdateResult updateResult = staticResourcesCollection.replaceOne(query, Objects.requireNonNull(RaadoUtils.<ProcessWiseConstants>convertToDocument(globalRates)));
        if (updateResult.getMatchedCount() != 0)
            return updateResult.wasAcknowledged();
        return staticResourcesCollection.insertOne(Objects.requireNonNull(RaadoUtils.<ProcessWiseConstants>convertToDocument(globalRates))).wasAcknowledged();
    }

    public boolean initializeGlobalStock() {
        final Document query = new Document().append("namespace", Constants.GLOBAL_STOCK);
        final Map<ProcessName, Map<String, Integer>> initialGlobalStock = new HashMap<>();
        Arrays.stream(ProcessName.values()).sequential()
                .forEach(processName -> initialGlobalStock.put(processName,
                        new HashMap<String, Integer>(){{ put(Constants.BAMBOO_STOCK, 0); }}));
        final ProcessWiseConstants globalStock = ProcessWiseConstants.builder().namespace(Constants.GLOBAL_STOCK).rates(initialGlobalStock).build();
        final UpdateResult updateResult = staticResourcesCollection.replaceOne(query, Objects.requireNonNull(RaadoUtils.<ProcessWiseConstants>convertToDocument(globalStock)));
        if (updateResult.getMatchedCount() != 0)
            return updateResult.wasAcknowledged();
        return staticResourcesCollection.insertOne(Objects.requireNonNull(RaadoUtils.<ProcessWiseConstants>convertToDocument(globalStock))).wasAcknowledged();
    }

    public boolean initializeProcessEntries() {
        final Document query = new Document().append("namespace", Constants.PROCESS_ENTRIES);
        Map<ProcessName, ArrayList<ProcessEntry>> processWiseEntries = new HashMap<>();
        Arrays.stream(ProcessName.values()).sequential()
                .forEach(processName -> processWiseEntries.put(processName, new ArrayList<>()));
        final ProcessEntries processEntries = ProcessEntries.builder().namespace(Constants.PROCESS_ENTRIES).processWiseEntries(processWiseEntries).build();
        UpdateResult updateResult = staticResourcesCollection.replaceOne(query, Objects.requireNonNull(RaadoUtils.<ProcessEntries>convertToDocument(processEntries)));
        if (updateResult.getMatchedCount() != 0)
            return updateResult.wasAcknowledged();
        return staticResourcesCollection.insertOne(Objects.requireNonNull(RaadoUtils.<ProcessEntries>convertToDocument(processEntries))).wasAcknowledged();
    }

    public Boolean updateProcessEntries(final String namespace,
                                        final ProcessName processName,
                                        final ArrayList<ProcessEntry> processEntries) {
        final Document query = new Document().append("namespace", namespace);
        boolean successfulUpdate = false;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final ProcessEntries oldProcessEntries = objectMapper.readValue(staticResourcesCollection.find(query).iterator().next().toJson(), ProcessEntries.class);
            oldProcessEntries.getProcessWiseEntries().put(processName, processEntries);
            successfulUpdate = staticResourcesCollection
                    .replaceOne(query, Objects.requireNonNull(RaadoUtils.<ProcessEntries>convertToDocument(oldProcessEntries))).wasAcknowledged();
            if(!successfulUpdate)
                return null;
        } catch (Exception me) {
            log.error("Error while updating user permissions =>" + me);
            throw new RaadoException("Network error please try after sometime.",
                    ErrorCode.INTERNAL_ERROR);
        }
        return successfulUpdate;
    }

    public Map<ProcessName, ArrayList<ProcessEntry>> getProcessWiseEntries() {
        ProcessEntries processEntries = new ProcessEntries();
        try {
            final Document query = new Document().append("namespace", Constants.PROCESS_ENTRIES);
            final ObjectMapper objectMapper = new ObjectMapper();
             processEntries = objectMapper.readValue(staticResourcesCollection.find(query).iterator().next().toJson(), ProcessEntries.class);
        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        return processEntries.getProcessWiseEntries();
    }
}
