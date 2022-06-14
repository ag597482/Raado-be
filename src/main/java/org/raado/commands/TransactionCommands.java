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
import org.raado.models.*;
import org.raado.utils.RaadoUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
public class TransactionCommands {

    private final MongoCollection<Document> transactionCollection;
    RaadoUtils<Transaction> raadoUtils;

    private final UserCommands userCommands;

    @Inject
    public TransactionCommands(@Named("transactionCollectionName") final String transactionCollectionName,
                               final MongoDatabase mdb,
                               final UserCommands userCommands) {
        this.userCommands = userCommands;
        this.transactionCollection =  mdb.getCollection(transactionCollectionName);
        raadoUtils = new RaadoUtils<Transaction>();
    }

    public Transaction addTransaction(final Transaction transaction) {
        String transactionId = "TN" + UUID.randomUUID();
        TimeZone.setDefault(TimeZone.getTimeZone("IST"));
        validateTransaction(transaction);
        transaction.setTransactionId(transactionId);
        transaction.setTimeOfTransaction(new Date());
        if(!transactionCollection.insertOne(raadoUtils.convertToDocument(transaction)).wasAcknowledged()) {
            throw new RaadoException("Network error please try after sometime.",
                    ErrorCode.INTERNAL_ERROR);
        }
        return transaction;
    }

    public Transaction updateTransaction(final String transactionId,
                                     final TransactionStatus transactionStatus,
                                     final String comment) {
        final Document query = new Document().append("transactionId",  transactionId);
        boolean successfulUpdate = false;
        final Transaction updatedTransaction;
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            updatedTransaction = objectMapper.readValue(transactionCollection.find().filter(query).iterator().next().toJson(), Transaction.class);
            TimeZone.setDefault(TimeZone.getTimeZone("IST"));
            updatedTransaction.setStatus(transactionStatus);
            updatedTransaction.setComment(comment);
            updatedTransaction.setTimeOfApproval(new Date());
            UpdateResult result = transactionCollection.replaceOne(query, raadoUtils.convertToDocument(updatedTransaction));
            successfulUpdate = result.wasAcknowledged();
        } catch (Exception me) {
            log.error("Error while updating user permissions =>" + me);
            throw new RaadoException("Network error please try after sometime.",
                    ErrorCode.INTERNAL_ERROR);
        }
        if (successfulUpdate) {
            return updatedTransaction;
        }
        return null;
    }

    public Transaction getTransactionById(final String transactionId) {
        final List<Transaction> allTransactions = getTransactions();
        final Transaction result = allTransactions.stream()
                .filter(user -> user.getTransactionId().equals(transactionId))
                .findFirst().orElse(null);
        if(Objects.isNull(result)) {
            throw new RaadoException("User not present.",
                    ErrorCode.INTERNAL_ERROR);
        }
        return result;
    }

    public List<Transaction> getTransactions() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final FindIterable<Document> iterable = transactionCollection.find();
        MongoCursor<Document> cursor = iterable.iterator();
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                transactions.add(objectMapper.readValue(cursor.next().toJson(), Transaction.class));
            }
        } catch (IOException e) {
            log.error("Error converting json to JAVA" , e);
        }
        return transactions;
    }

    private void validateTransaction(final Transaction transaction) {
        if (transaction.getFromProcess() == transaction.getToProcess()) {
            throw new RaadoException("FROM process can't be TO process.",
                    ErrorCode.INTERNAL_ERROR);
        }
        if (Objects.equals(transaction.getFromUserId(), transaction.getToUserId())) {
            throw new RaadoException("FROM user can't be TO user.",
                    ErrorCode.INTERNAL_ERROR);
        }
        User fromUser = userCommands.getUserById(transaction.getFromUserId());
        User toUser = userCommands.getUserById(transaction.getToUserId());
        if (validateUserPermissions(fromUser, transaction.getFromProcess()) && validateUserPermissions(toUser, transaction.getToProcess())) {
            transaction.setFromUserName(fromUser.getName());
            transaction.setToUserName(toUser.getName());
            return;
        }
        throw new RaadoException("Users don't have write permissions.",
                ErrorCode.PERMISSIONS_ACCESS_ISSUE);
    }

    private boolean validateUserPermissions(final User user, final ProcessName processName) {
        Optional<Permission> userPermission = user.getPermissions().stream().filter(permission -> permission.getProcessName().equals(processName)).findFirst();
        return userPermission.isPresent() && userPermission.get().isWrite();
    }
}
