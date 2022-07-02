package org.raado.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
        StaticRates globalRates = new StaticRates();
        try {
            final Document query = new Document().append("namespace", Constants.GLOBAL_RATES);
            final ObjectMapper objectMapper = new ObjectMapper();
            globalRates = objectMapper.readValue(staticResourcesCollection.find(query).iterator().next().toJson(), StaticRates.class);

        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        return globalRates.getRates();
    }

    public Boolean updateGlobalRate(final String namespace,
                                    final ProcessName processName,
                                    final Map<String, Integer> entriesRate) {
        final Document query = new Document().append("namespace", namespace);
        boolean successfulUpdate = false;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final StaticRates globalRates = objectMapper.readValue(staticResourcesCollection.find(query).iterator().next().toJson(), StaticRates.class);
            globalRates.getRates().put(processName, entriesRate);
            successfulUpdate = staticResourcesCollection
                    .replaceOne(query, Objects.requireNonNull(RaadoUtils.<StaticRates>convertToDocument(globalRates))).wasAcknowledged();
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
        Map<ProcessName, Map<String, Integer>> initialGlobalRates = new HashMap<>();
        Arrays.stream(ProcessName.values()).sequential()
                .forEach(processName -> initialGlobalRates.put(processName, new HashMap<>()));
        StaticRates globalRates = StaticRates.builder().namespace(Constants.GLOBAL_RATES).rates(initialGlobalRates).build();
        return staticResourcesCollection.insertOne(Objects.requireNonNull(RaadoUtils.<StaticRates>convertToDocument(globalRates))).wasAcknowledged();
    }

    public boolean initializeProcessEntries() {
        Map<ProcessName, ArrayList<String>> processWiseEntries = new HashMap<>();
        Arrays.stream(ProcessName.values()).sequential()
                .forEach(processName -> processWiseEntries.put(processName, new ArrayList<>()));
        ProcessEntries processEntries = ProcessEntries.builder().namespace(Constants.PROCESS_ENTRIES).processWiseEntries(processWiseEntries).build();
        return staticResourcesCollection.insertOne(Objects.requireNonNull(RaadoUtils.<ProcessEntries>convertToDocument(processEntries))).wasAcknowledged();
    }

    public Boolean updateProcessEntries(final String namespace,
                                        final ProcessName processName,
                                        final ArrayList<String> processEntries) {
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

    public Map<ProcessName, ArrayList<String>> getProcessWiseEntries() {
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
