package org.raado.commands;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;
import org.raado.configs.CacheConfig;
import org.raado.exceptions.ErrorCode;
import org.raado.exceptions.RaadoException;
import org.raado.models.Transaction;
import org.raado.models.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class LocalCacheCommands implements Managed {

    private static final String ALL_USERS = "ALL_USERS";
    private static final String ALL_TRANSACTIONS = "ALL_TRANSACTIONS";
    private final UserCommands userCommands;
    private final TransactionCommands transactionCommands;
    private final CacheConfig cacheConfig;

    private AsyncLoadingCache<String, Map<String, User>> usersCache;

    private AsyncLoadingCache<String, Map<String, Transaction>> transactionsCache;

    @Inject
    public LocalCacheCommands(final UserCommands userCommands,
                              final TransactionCommands transactionCommands,
                              final CacheConfig cacheConfig) {
        this.userCommands = userCommands;
        this.transactionCommands = transactionCommands;
        this.cacheConfig = cacheConfig;
    }

    public Map<String, User> getAllUsers() {
        try {
            return usersCache.get(ALL_USERS).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RaadoException(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        } catch(Exception e) {
            return Collections.emptyMap();
        }
    }

    public Map<String, Transaction> getAllTransactions() {
        try {
            return transactionsCache.get(ALL_TRANSACTIONS).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RaadoException(e.getMessage(), ErrorCode.INTERNAL_ERROR);
        } catch(Exception e) {
            return Collections.emptyMap();
        }
    }

    public void refreshUsersCache(final User user) {
        final Map<String, User> cachedUsers = getAllUsers();
        cachedUsers.put(user.getUserId(), user);
        usersCache.put(ALL_USERS, CompletableFuture.completedFuture(cachedUsers));
    }

    public void refreshTransactionsCache(final Transaction transaction) {
        final Map<String, Transaction> cachedTransactions = getAllTransactions();
        cachedTransactions.put(transaction.getTransactionId(), transaction);
        transactionsCache.put(ALL_TRANSACTIONS, CompletableFuture.completedFuture(cachedTransactions));
    }

    @Override
    public void start() throws Exception {
        log.info("Initializing local cache with config {}", cacheConfig);
        usersCache = Caffeine.newBuilder()
                .maximumSize(cacheConfig.getMaxElements())
                .refreshAfterWrite(cacheConfig.getRefreshInMinutes(), TimeUnit.MINUTES)
                .buildAsync(key -> {
                    log.info("Loading data for users");
                    return userCommands.getUsers().stream()
                            .collect(Collectors.toMap(User::getUserId, x->x));
                });
        usersCache.get(ALL_USERS).get();

        transactionsCache = Caffeine.newBuilder()
                .maximumSize(cacheConfig.getMaxElements())
                .refreshAfterWrite(cacheConfig.getRefreshInMinutes(), TimeUnit.MINUTES)
                .buildAsync(key -> {
                    log.info("Loading data for transactions");
                    return transactionCommands.getTransactions().stream()
                            .collect(Collectors.toMap(Transaction::getTransactionId, x->x));
                });
        transactionsCache.get(ALL_TRANSACTIONS).get();
    }


    @Override
    public void stop() throws Exception {

    }
}
