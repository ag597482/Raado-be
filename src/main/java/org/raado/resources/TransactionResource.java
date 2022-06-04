package org.raado.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import org.raado.models.ProcessName;
import org.raado.models.Transaction;
import org.raado.models.TransactionStatus;
import org.raado.services.TransactionService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Api("Transaction Management APIs")
public class TransactionResource {

    private final TransactionService transactionService;

    @Inject
    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/addTransaction")
    public Boolean addTransaction(@Valid Transaction transaction) {
        return transactionService.addTransaction(transaction);
    }

    @PATCH
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/updateTransaction")
    public Boolean updateTransaction(@QueryParam("transactionId") final String transactionId, @Valid Transaction transaction) {
        return transactionService.updateTransaction(transactionId, transaction);
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getTransactions")
    public List<Transaction> getTransactions() {
        return transactionService.getAllTransactions();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getFilteredTransactions")
    public List<Transaction> getFilteredTransactions(@QueryParam("fromProcess") ProcessName fromProcess,
                                                     @QueryParam("toProcess")  ProcessName toProcess,
                                                     @QueryParam("fromUserPhone") String fromUserPhone,
                                                     @QueryParam("toUserPhone") String toUserPhone,
                                                     @QueryParam("status") TransactionStatus status) {
        return transactionService.getFilteredTransactions(fromProcess, toProcess, fromUserPhone, toUserPhone, status);
    }
}