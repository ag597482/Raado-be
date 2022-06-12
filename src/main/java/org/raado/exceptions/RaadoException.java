package org.raado.exceptions;

import lombok.Data;

@Data
public class RaadoException extends RuntimeException {

    private final ErrorCode errorCode;

    public RaadoException(final String message, final ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
