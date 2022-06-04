package org.raado.services;

import com.google.inject.Inject;
import org.raado.commands.TransactionCommands;
import org.raado.models.ProcessName;
import org.raado.models.Transaction;
import org.raado.models.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionService {

    private TransactionCommands transactionCommands;

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

    public boolean updateTransaction(final String transactionId, final Transaction transaction) {
//        final Transaction transaction = getAllTransactions().stream()
//                .filter(savedTransaction -> Objects.equals(savedTransaction.getTransactionId(), transactionId)).toList().
//                get(0);
//        if (transaction == null)
//            return false;
        return transactionCommands.updateTransaction(transactionId, transaction);
    }

    public List<Transaction> getFilteredTransactions(final ProcessName fromProcess,
                                                     final ProcessName toProcess,
                                                     final String fromUserPhone,
                                                     final String toUserPhone,
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
        if(fromUserPhone!=null && fromUserPhone.length()>0)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getFromUserPhone().equals(fromUserPhone))
                    .collect(Collectors.toList());
        if(toUserPhone!=null && toUserPhone.length()>0)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getToUserPhone().equals(toUserPhone))
                    .collect(Collectors.toList());
        if(status != null)
            filteredTransactions = filteredTransactions.stream()
                    .filter(transaction -> transaction.getStatus() == status)
                    .collect(Collectors.toList());
        return filteredTransactions;
    }
}
