package org.raado.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.inject.Inject;
import com.sun.el.parser.BooleanNode;
import io.swagger.annotations.Api;
import org.raado.models.Permission;
import org.raado.models.User;
import org.raado.services.UserService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Objects;


@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Api("User Management APIs")
public class UserResource {
    private final String defaultName;

    private final UserService userService;

    @Inject
    public UserResource(final String defaultName, final UserService userService) {
        this.defaultName = defaultName;
        this.userService = userService;
    }

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonNode sayHello(@QueryParam("name") final String input) {
        final String value = Objects.isNull(input) ? defaultName : input;
        return new TextNode(value);
    }

    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/addUser")
    public Boolean addUser(@Valid User user) {
        return this.userService.addUser(user);
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getUsers")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PATCH
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/updatePermissions")
    public Boolean updateUserPermissions(@QueryParam("phoneNo") String phoneNo, @Valid List<Permission> permissions) {
        return userService.updateUserPermissions(phoneNo, permissions);
    }
}