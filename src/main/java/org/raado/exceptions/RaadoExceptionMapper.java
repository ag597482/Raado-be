package org.raado.exceptions;

import org.raado.response.RaadoResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class RaadoExceptionMapper implements ExceptionMapper<RaadoException> {
    @Override
    public Response toResponse(RaadoException e) {
        Response response;
        switch (e.getErrorCode()) {
            case INTERNAL_ERROR:
            case PASSWORD_IS_INCORRECT:
            case PHONE_NUMBER_ALREADY_EXISTS:
            case USERID_OR_PASSWORD_IS_INCORRECT:
            default:
                response = Response.status(400)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .entity(RaadoResponse.builder()
                                .data(e.getMessage())
                                .errorCode(e.getErrorCode())
                                .success(false)
                                .build())
                        .build();
        }
        return response;
    }
}
