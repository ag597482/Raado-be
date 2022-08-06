package org.raado.resources;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import org.raado.commands.StaticCommands;
import org.raado.exceptions.ErrorCode;
import org.raado.exceptions.RaadoException;
import org.raado.models.Constants;
import org.raado.models.ProcessEntry;
import org.raado.models.ProcessName;
import org.raado.response.RaadoResponse;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Path("/staticResource")
@Produces(MediaType.APPLICATION_JSON)
@Api("Static Resource APIs")
public class StaticResource {

    private final StaticCommands staticCommands;

    @Inject
    public StaticResource(final StaticCommands staticCommands) {
        this.staticCommands = staticCommands;
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getGlobalRates")
    public RaadoResponse<Map<ProcessName, Map<String, Integer>>> getGlobalRates() {
        return RaadoResponse.<Map<ProcessName, Map<String, Integer>>>builder()
                .success(true)
                .data(staticCommands.getGlobalRates())
                .build();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getGlobalStocks")
    public RaadoResponse<Map<ProcessName, Map<String, Integer>>> getGlobalStocks() {
        return RaadoResponse.<Map<ProcessName, Map<String, Integer>>>builder()
                .success(true)
                .data(staticCommands.getGlobalStock())
                .build();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getProcessWiseEntries")
    public RaadoResponse<Map<ProcessName, ArrayList<ProcessEntry>>> getProcessWiseEntries() {
        return RaadoResponse.<Map<ProcessName, ArrayList<ProcessEntry>>>builder()
                .success(true)
                .data(staticCommands.getProcessWiseEntries())
                .build();
    }

    @PATCH
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/{processName}/updateGlobalRate")
    public RaadoResponse<Boolean> updateUserProcessRate(@PathParam("processName") ProcessName processName, @Valid Map<String, Integer> entriesRate) {
        if (Objects.isNull(entriesRate)) {
            throw new RaadoException("entries can not be null",
                    ErrorCode.CANNOT_BE_NULL);
        }
        return RaadoResponse.<Boolean>builder()
                .success(true)
                .data(staticCommands.updateGlobalProcessWiseConstants(Constants.GLOBAL_RATES, processName, entriesRate))
                .build();
    }

    @PATCH
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/{processName}/updateProcessEntries")
    public RaadoResponse<Boolean> updateUserProcessEntries(@PathParam("processName") ProcessName processName,
                                                           @QueryParam("namespace") String namespace,
                                                           @Valid ArrayList<ProcessEntry> processEntries) {
        if (Objects.isNull(namespace) || Objects.isNull(processEntries)) {
            throw new RaadoException("namespace and it's entries can not be null",
                    ErrorCode.CANNOT_BE_NULL);
        }
        if (!namespace.equals(Constants.PROCESS_ENTRIES)) {
            throw new RaadoException("namespace should be PROCESS_ENTRIES for static resources",
                    ErrorCode.INTERNAL_ERROR);
        }
        return RaadoResponse.<Boolean>builder()
                .success(true)
                .data(staticCommands.updateProcessEntries(namespace, processName, processEntries))
                .build();
    }
}
