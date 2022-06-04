package org.raado.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoDatabase;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;
import org.raado.models.Transaction;
import org.raado.models.User;

import java.util.ArrayList;
import java.util.List;

public class TransactionCommands {

    private JacksonDBCollection<Transaction, String> jacksonDBCollection;

    @Inject
    public TransactionCommands(@Named("transactionCollectionName") final String transactionCollectionName, final MongoDatabase mdb) {
        //this.jacksonDBCollection = JacksonDBCollection.wrap(db.getCollection(transactionCollectionName), Transaction.class, String.class);
        mdb.getCollection(transactionCollectionName);
        //this.jacksonDBCollection = JacksonDBCollection.wrap(dbCollection, Transaction.class, String.class);
    }

    public boolean addTransaction(final Transaction transaction) {
        final WriteResult writeResult = jacksonDBCollection.insert(transaction);
        return writeResult.getError() == null;
    }

    public boolean updateTransaction(final String transactionId, final Transaction updatedTransaction) {
        BasicDBObject searchQuery = new BasicDBObject().append("_id", "ObjectId(" + transactionId + ")");
        //Transaction transaction = jacksonDBCollection.find(searchQuery).next();
        final WriteResult writeResult = jacksonDBCollection.update(jacksonDBCollection.findOneById(transactionId), updatedTransaction);
        return writeResult.getError() == null;
    }

    public List<Transaction> getTransactions() {
        DBCursor<Transaction> dbCursor = jacksonDBCollection.find();
        List<Transaction> datas = new ArrayList<>();
        while (dbCursor.hasNext()) {
            Transaction data = dbCursor.next();
            datas.add(data);
        }
        return datas;
    }
}
