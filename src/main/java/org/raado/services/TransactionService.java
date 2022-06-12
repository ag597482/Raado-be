package org.raado.services;

import com.google.inject.Inject;
import org.raado.commands.TransactionCommands;
import org.raado.models.ProcessName;
import org.raado.models.Transaction;
import org.raado.models.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionService {

    private final TransactionCommands transactionCommands;

    @Inject
    public TransactionService(final TransactionCommands transactionCommands) {
        this.transactionCommands = transactionCommands;
    }

    public boolean addTransaction(final Transaction transaction) {
        return transactionCommands.addTransaction(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionCommands.getTransactions();
    }

    public boolean updateTransaction(final String transactionId, final TransactionStatus transactionStatus, final String comment) {
        return transactionCommands.updateTransaction(transactionId, transactionStatus, comment);
    }

    public List<Transaction> getFilteredTransactions(final ProcessName fromProcess,
                                                     final ProcessName toProcess,
                                                     final String fromUserId,
                                                     final String toUserId,
                                                     final TransactionStatus status) {
        List<Transaction> allTransactions = transactionCommands.getTransactions();
        List<Transaction> filteredTransactions = new ArrayList<>(allTransactions);
        if(fromProcess!=null)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getFromProcess().equals(fromProcess))
                    .collect(Collectors.toList());
        if(toProcess!=null)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getToProcess().equals(toProcess))
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
