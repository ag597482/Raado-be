package org.raado.response;

import lombok.Builder;
import lombok.Data;
import org.raado.exceptions.ErrorCode;

@Data
@Builder
public class RaadoResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorCode errorCode;
}
