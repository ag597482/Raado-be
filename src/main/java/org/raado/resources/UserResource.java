package org.raado.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import lombok.NonNull;
import org.raado.exceptions.ErrorCode;
import org.raado.exceptions.RaadoException;
import org.raado.models.Permission;
import org.raado.models.User;
import org.raado.response.RaadoResponse;
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
    public RaadoResponse<User> addUser(@Valid User user) {
        if (Objects.isNull(user.getName()) || Objects.isNull(user.getPassword()) || Objects.isNull(user.getPhoneNo())) {
            throw new RaadoException("name, password, phone Number can not be null",
                    ErrorCode.CANNOT_BE_NULL);
        }
        return RaadoResponse.<User>builder()
                .success(true)
                .data(this.userService.addUser(user))
                .build();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/getUsers")
    public RaadoResponse<List<User>> getUsers() {
        return RaadoResponse.<List<User>>builder()
                .success(true)
                .data(userService.getAllUsers())
                .build();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/validateUser")
    public RaadoResponse<User> validateUser(@QueryParam("phoneNo") String phoneNo, @QueryParam("password") String password) {
        if (Objects.isNull(password) || Objects.isNull(phoneNo)) {
            throw new RaadoException("phoneNo and Password can not be null",
                    ErrorCode.CANNOT_BE_NULL);
        }
        return RaadoResponse.<User>builder()
                .success(true)
                .data(userService.validateUser(phoneNo, password))
                .build();
    }

    @PATCH
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/updatePermissions")
    public RaadoResponse<Boolean> updateUserPermissions(@QueryParam("userId") String userId, @Valid List<Permission> permissions) {
        if (Objects.isNull(userId)) {
            throw new RaadoException("userId can not be null",
                    ErrorCode.CANNOT_BE_NULL);
        }
        return RaadoResponse.<Boolean>builder()
                .success(true)
                .data(userService.updateUserPermissions(userId, permissions))
                .build();
    }
}