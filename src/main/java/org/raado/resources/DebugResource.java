package org.raado.resources;

import com.google.inject.Inject;
import io.swagger.annotations.Api;
import org.raado.commands.LocalCacheCommands;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/debug")
@Produces(MediaType.APPLICATION_JSON)
@Api("Debug APIs")
public class DebugResource {

    private final LocalCacheCommands localCacheCommands;

    @Inject
    public DebugResource(final LocalCacheCommands localCacheCommands){
        this.localCacheCommands = localCacheCommands;
    }

    @GET
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean refreshCache() {
        localCacheCommands.hardRefreshCache();
        return true;
    }
}
