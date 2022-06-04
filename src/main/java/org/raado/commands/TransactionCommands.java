package org.raado.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.WriteResult;
import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import org.raado.models.Transaction;
import org.raado.utils.RaadoUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.reflections.Reflections.log;

public class TransactionCommands {

    private MongoCollection<Document> transactionCollection;
    RaadoUtils<Transaction> raadoUtils;

    @Inject
    public TransactionCommands(@Named("transactionCollectionName") final String transactionCollectionName, final MongoDatabase mdb) {
        this.transactionCollection =  mdb.getCollection(transactionCollectionName);
        raadoUtils = new RaadoUtils<Transaction>();
    }

    public boolean addTransaction(final Transaction transaction) {
        return transactionCollection.insertOne(raadoUtils.convertToDocument(transaction)).wasAcknowledged();
    }

    public boolean updateTransaction(final String transactionId, final Transaction updatedTransaction) {
//        BasicDBObject searchQuery = new BasicDBObject().append("_id", "ObjectId(" + transactionId + ")");
//        //Transaction transaction = jacksonDBCollection.find(searchQuery).next();
//        final WriteResult writeResult = jacksonDBCollection.update(jacksonDBCollection.findOneById(transactionId), updatedTransaction);
//        return writeResult.getError() == null;
        return false;
    }

    public List<Transaction> getTransactions() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final FindIterable<Document> iterable = transactionCollection.find();
        MongoCursor<Document> cursor = iterable.iterator();
        System.out.println("Student list with a cursor: ");
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                //log.info(cursor.next().toJson());
                transactions.add(objectMapper.readValue(cursor.next().toJson(), Transaction.class));
            }
        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        return transactions;
    }
}
