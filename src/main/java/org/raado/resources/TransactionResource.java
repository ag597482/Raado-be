package org.raado.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import org.raado.models.ProcessName;
import org.raado.models.Transaction;
import org.raado.models.TransactionStatus;
import org.raado.response.RaadoResponse;
import org.raado.services.TransactionService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    public RaadoResponse<Boolean> addTransaction(@Valid Transaction transaction) {
        return RaadoResponse.<Boolean>builder()
                .success(true)
                .data(transactionService.addTransaction(transaction))
                .build();
    }

    @PATCH
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/updateTransaction")
    public RaadoResponse<Boolean> updateTransaction(@QueryParam("transactionId") final String transactionId,
                                                    @QueryParam("transactionStatus") final TransactionStatus transactionStatus,
                                                    @QueryParam("comment") final String comment) {
        return RaadoResponse.<Boolean>builder()
                .success(true)
                .data(transactionService.updateTransaction(transactionId, transactionStatus, comment))
                .build();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getTransactions")
    public RaadoResponse<List<Transaction>> getTransactions() {
        return RaadoResponse.<List<Transaction>>builder()
                .success(true)
                .data(transactionService.getAllTransactions())
                .build();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getFilteredTransactions")
    public RaadoResponse<List<Transaction>> getFilteredTransactions(@QueryParam("fromProcess") ProcessName fromProcess,
                                                     @QueryParam("toProcess")  ProcessName toProcess,
                                                     @QueryParam("commonProcess")  ProcessName commonProcess,
                                                     @QueryParam("fromUserId") String fromUserId,
                                                     @QueryParam("toUserId") String toUserId,
                                                     @QueryParam("status") TransactionStatus status) {
        return RaadoResponse.<List<Transaction>>builder()
                .success(true)
                .data(transactionService.getFilteredTransactions(fromProcess, toProcess, commonProcess,fromUserId, toUserId, status))
                .build();
    }
}