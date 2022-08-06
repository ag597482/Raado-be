package org.raado.resources;

import com.google.inject.Inject;
import io.swagger.annotations.Api;
import org.raado.commands.LocalCacheCommands;
import org.raado.commands.StaticCommands;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/debug")
@Produces(MediaType.APPLICATION_JSON)
@Api("Debug APIs")
public class DebugResource {

    private final LocalCacheCommands localCacheCommands;

    private final StaticCommands staticCommands;

    @Inject
    public DebugResource(final LocalCacheCommands localCacheCommands, StaticCommands staticCommands){
        this.localCacheCommands = localCacheCommands;
        this.staticCommands = staticCommands;
    }

    @GET
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean refreshCache() {
        localCacheCommands.hardRefreshCache();
        return true;
    }

    @GET
    @Path("/initializeGlobalRates")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean initializeGlobalRates() {
        return staticCommands.initializeGlobalRates() && staticCommands.initializeGlobalStock();
    }

    @GET
    @Path("/initializeProcessEntries")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean initializeProcessEntries() {
        return staticCommands.initializeProcessEntries();
    }
}
