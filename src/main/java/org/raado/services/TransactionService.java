package org.raado.services;

import com.google.inject.Inject;
import org.raado.commands.LocalCacheCommands;
import org.raado.commands.TransactionCommands;
import org.raado.models.ProcessName;
import org.raado.models.Transaction;
import org.raado.models.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TransactionService {

    private final TransactionCommands transactionCommands;

    private final LocalCacheCommands localCacheCommands;

    @Inject
    public TransactionService(final TransactionCommands transactionCommands,
                              final LocalCacheCommands localCacheCommands) {
        this.transactionCommands = transactionCommands;
        this.localCacheCommands = localCacheCommands;
    }

    public boolean addTransaction(final Transaction transaction) {
        final Transaction newTransaction = transactionCommands.addTransaction(transaction);
        localCacheCommands.refreshTransactionsCache(newTransaction);
        return newTransaction.getTransactionId() != null;
    }

    public List<Transaction> getAllTransactions() {
        return localCacheCommands.getAllTransactions().values().stream().toList();
        //return transactionCommands.getTransactions();
    }

    public boolean updateTransaction(final String transactionId, final TransactionStatus transactionStatus, final String comment) {
        final Transaction updatedTransaction = transactionCommands.updateTransaction(transactionId, transactionStatus, comment);
        if (Objects.nonNull(updatedTransaction)) {
            localCacheCommands.getAllTransactions().put(transactionId, updatedTransaction);
            return true;
        }
        return false;
    }

    public List<Transaction> getFilteredTransactions(final ProcessName fromProcess,
                                                     final ProcessName toProcess,
                                                     final ProcessName commonProcess,
                                                     final String fromUserId,
                                                     final String toUserId,
                                                     final TransactionStatus status) {
        final List<Transaction> allTransactions = getAllTransactions();
        List<Transaction> filteredTransactions = new ArrayList<>(allTransactions);
        if(fromProcess!=null)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getFromProcess().equals(fromProcess))
                    .collect(Collectors.toList());
        if(toProcess!=null)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getToProcess().equals(toProcess))
                    .collect(Collectors.toList());
        if(commonProcess!=null)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getFromProcess().equals(commonProcess) || transaction.getToProcess().equals(commonProcess))
                    .collect(Collectors.toList());
        if(fromUserId!=null && fromUserId.length()>0)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getFromUserId().equals(fromUserId))
                    .collect(Collectors.toList());
        if(toUserId!=null && toUserId.length()>0)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getToUserId().equals(toUserId))
                    .collect(Collectors.toList());
        if(status != null)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getStatus() == status)
                    .collect(Collectors.toList());
        return filteredTransactions;
    }
}
